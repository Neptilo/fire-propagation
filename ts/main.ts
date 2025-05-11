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

// Make the table fit within the available space while keeping the tiles square
// and define body margins
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

// Calculate the additional vertical space taken by the start button
function getButtonHeight(): number {
    if (!startButton) return 0;
    const marginBottom = parseFloat(getComputedStyle(startButton).marginBottom);
    return startButton.offsetHeight + marginBottom;
}

/* data helpers */

// Get the initial simulation data (= map size)
// and use it to initialize the Vue data
async function fetchInitialData() {
    const res = await fetch("/api/map-size");
    const initialData = await res.json();
    vm.width = initialData.width;
    vm.height = initialData.height;
    vm.map = Array.from({ length: vm.height }, () =>
        Array(vm.width).fill(Tile.Tree));
}

// Get the cell changes since last update and use it to update Vue's map data
async function updateMap() {
    const res = await fetch("/api/diff");
    const diffData = await res.json();
    for (let [x, y] of diffData.fire)
        vm.map[x][y] = Tile.Fire;
    for (let [x, y] of diffData.ash)
        vm.map[x][y] = Tile.Ash;
}

/* initialization */

// Get the initial data and use it to layout the view
async function initApp() {
    await fetchInitialData();
    layout();
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