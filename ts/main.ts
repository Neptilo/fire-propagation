/* constants */

const MARGIN = 16;
const REFRESH_RATE = 200;

const startButton = document.getElementById('start-button') as HTMLButtonElement;

/* type definitions */

// must match the definition in the backend, in the same order
enum Tile { Tree, Fire, Ash };

type VueData = {
    width: number;
    height: number;
    map: Tile[][];
};

/* Vue setup */

const app = Vue.createApp({
    data(): VueData {
        return {
            width: 0,
            height: 0,
            map: []
        };
    },
    methods: {
        getTileName(tile: Tile) {
            switch (tile) {
                case Tile.Tree: return 'tree';
                case Tile.Fire: return 'fire';
                case Tile.Ash: return 'ash';
            }
        }
    }
});

const vm = app.mount('#app') as VueInstance<VueData>;

/* layout helpers */

function layout() {
    const tableElem = document.querySelector('table')!;
    const maxWidth = window.innerWidth - 2 * MARGIN;
    const buttonHeight = getButtonHeight();
    const maxHeight = window.innerHeight - 2 * MARGIN - buttonHeight;

    const mapAspectRatio = vm.width / vm.height;
    const spaceAspectRatio = maxWidth / maxHeight;

    if (mapAspectRatio <= spaceAspectRatio) {
        tableElem.style.height = `${maxHeight}px`;
        tableElem.style.width = `${maxHeight * mapAspectRatio}px`;
    } else {
        tableElem.style.width = `${maxWidth}px`;
        tableElem.style.height = `${maxWidth / mapAspectRatio}px`;
    }

    document.body.style.margin = `${MARGIN}px`;
}

function getButtonHeight(): number {
    if (!startButton) return 0;
    const marginBottom = parseFloat(getComputedStyle(startButton).marginBottom);
    return startButton.offsetHeight + marginBottom;
}

/* data helpers */

async function fetchInitialData() {
    const res = await fetch("/api/map-size");
    const initialData = await res.json();
    vm.width = initialData.width;
    vm.height = initialData.height;
    vm.map = Array.from({ length: vm.height }, () =>
        Array(vm.width).fill(Tile.Tree));
}

async function updateMap() {
    const res = await fetch("/api/diff");
    const diffData = await res.json();
    for (let [x, y] of diffData.fire)
        vm.map[x][y] = Tile.Fire;
    for (let [x, y] of diffData.ash)
        vm.map[x][y] = Tile.Ash;
}

/* initialization */

async function initApp() {
    await fetchInitialData();
    layout();
    updateMap(); // update map data immediately
}

/* event binding */

window.onload = initApp;
window.onresize = layout;

if (startButton) {
    startButton.onclick = () => {
        fetch("/api/start", { method: 'POST' }); // start simulation
        setInterval(updateMap, REFRESH_RATE); // schedule periodical data updates
        startButton.disabled = true;
    };
}