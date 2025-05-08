const tableElem = document.getElementsByTagName('table')[0];
const rowElem = document.createElement('tr');
tableElem.appendChild(rowElem);
const cellElem = document.createElement('td');
rowElem.appendChild(cellElem);

const margin = 32;
tableElem.style.width = window.innerWidth - 2 * margin + 'px';
tableElem.style.height = window.innerHeight - 2 * margin + 'px';