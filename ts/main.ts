const tableElem = document.getElementsByTagName('table')[0];
const rowElem = document.createElement('tr');
tableElem.appendChild(rowElem);
const cellElem = document.createElement('td');
rowElem.appendChild(cellElem);

const margin = 32;
tableElem.style.width = window.innerWidth - 2 * margin + 'px';
tableElem.style.height = window.innerHeight - 2 * margin + 'px';

type VueData = {
    counter: number;
};

const app = Vue.createApp({
    data(): VueData {
        return {
            counter: 0
        }
    }
});

const vm = app.mount('#app') as VueInstance<VueData>;

setInterval(async () => {
    const res = await fetch("/api/data");
    const text = await res.text();
    vm.counter = parseInt(text);
}, 200);
