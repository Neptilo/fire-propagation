const tableElem = document.getElementsByTagName('table')[0];

const margin = 32;
tableElem.style.width = window.innerWidth - 2 * margin + 'px';
tableElem.style.height = window.innerHeight - 2 * margin + 'px';

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

    // for now, diffData only contains the new fire tiles
    for (let tile of diffData)
        vm.map[tile[0]][tile[1]] = Tile.Fire;
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
    updateMap();

    // periodically update data
    setInterval(updateMap, 200);
}