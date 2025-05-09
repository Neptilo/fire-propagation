const tableElem = document.getElementsByTagName('table')[0];

const margin = 32;
tableElem.style.width = window.innerWidth - 2 * margin + 'px';
tableElem.style.height = window.innerHeight - 2 * margin + 'px';

// must match the definition in the backend, in the same order
enum Tile { Tree, Fire, Dead };

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
                case Tile.Dead:
                    return 'dead';
            }
        }
    }
});

const vm = app.mount('#app') as VueInstance<VueData>;

// initialize data
onload = async () => {
    const res = await fetch("/api/map-size");
    const initialData = await res.json();
    vm.width = initialData.width;
    vm.height = initialData.height;
    vm.map = Array.from(
        { length: vm.height },
        () => Array(vm.width).fill(Tile.Tree));
    vm.map[0][0] = Tile.Fire; // initilization test
}