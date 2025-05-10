const MARGIN = 16;

// must match the definition in the backend, in the same order
enum Tile { Tree, Fire, Ash };

type VueData = {
    width: number;
    height: number;
    map: Tile[][];
};

const app = Vue.createApp({
    data(): VueData {
        return {
            width: 0,
            height: 0,
            map: []
        }
    },
    methods: {
        getTileName(tile: Tile) {
            switch (tile) {
                case Tile.Tree:
                    return 'tree';
                case Tile.Fire:
                    return 'fire';
                case Tile.Ash:
                    return 'ash';
            }
        }
    }
});

const vm = app.mount('#app') as VueInstance<VueData>;

async function updateMap() {
    const res = await fetch("/api/diff");
    const diffData = await res.json();
    for (let tile of diffData.fire)
        vm.map[tile[0]][tile[1]] = Tile.Fire;
    for (let tile of diffData.ash)
        vm.map[tile[0]][tile[1]] = Tile.Ash;
}

function resize() {
    // fit table into available space, keeping tiles square
    const tableElem = document.getElementsByTagName('table')[0];
    const maxWidth = window.innerWidth - 2 * MARGIN;
    const maxHeight = window.innerHeight - 2 * MARGIN;
    const mapAspectRatio = vm.width / vm.height;
    const spaceAspectRatio = maxWidth / maxHeight;
    if (mapAspectRatio <= spaceAspectRatio) {
        // fill height
        tableElem.style.height = maxHeight + 'px';
        tableElem.style.width = maxHeight * mapAspectRatio + 'px';
    } else {
        // fill width
        tableElem.style.width = maxWidth + 'px';
        tableElem.style.height = maxWidth / mapAspectRatio + 'px';
    }
    document.body.style.margin = MARGIN + 'px';
}

// initialize data
onload = async () => {
    const res = await fetch("/api/map-size");
    const initialData = await res.json();
    vm.width = initialData.width;
    vm.height = initialData.height;
    vm.map = Array.from(
        { length: vm.height },
        () => Array(vm.width).fill(Tile.Tree));

    resize();

    updateMap(); // update map data immediately
    setInterval(updateMap, 200); // schedule periodical data updates
}

onresize = resize;