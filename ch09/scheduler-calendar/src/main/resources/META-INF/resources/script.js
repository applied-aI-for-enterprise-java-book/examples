const daysContainer = document.querySelector('.days');
const monthYear = document.getElementById('month-year');
const prevMonthBtn = document.getElementById('prev-month');
const nextMonthBtn = document.getElementById('next-month');
const eventModal = document.getElementById('event-modal');
const closeModal = document.querySelector('.close');
const saveEventBtn = document.getElementById('save-event');
const eventTitleInput = document.getElementById('event-title');

let currentDate = new Date();
let events = {};

// WebSocket connection
const socket = new WebSocket('ws://localhost:3000');

socket.onmessage = function(event) {
    const eventData = JSON.parse(event.data);
    events[eventData.date] = eventData.title;
    renderCalendar();
};

function renderCalendar() {
    daysContainer.innerHTML = '';
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();

    monthYear.innerText = `${currentDate.toLocaleString('default', { month: 'long' })} ${year}`;

    const firstDay = new Date(year, month, 1).getDay();
    const totalDays = new Date(year, month + 1, 0).getDate();

    for (let i = 0; i < firstDay; i++) {
        daysContainer.innerHTML += `<div></div>`;
    }

    for (let i = 1; i <= totalDays; i++) {
        const dayString = `${year}-${month + 1}-${i}`;
        daysContainer.innerHTML += `
            <div class="day" data-date="${dayString}">
                ${i}
                ${events[dayString] ? `<span class="event">${events[dayString]}</span>` : ''}
            </div>
        `;
    }

    document.querySelectorAll('.day').forEach(day => {
        day.addEventListener('click', () => {
            eventModal.style.display = 'flex';
            saveEventBtn.onclick = () => {
                const title = eventTitleInput.value;
                if (title) {
                    events[day.dataset.date] = title;
                    socket.send(JSON.stringify({ date: day.dataset.date, title }));
                    renderCalendar();
                    eventModal.style.display = 'none';
                }
            };
        });
    });
}

prevMonthBtn.addEventListener('click', () => {
    currentDate.setMonth(currentDate.getMonth() - 1);
    renderCalendar();
});

nextMonthBtn.addEventListener('click', () => {
    currentDate.setMonth(currentDate.getMonth() + 1);
    renderCalendar();
});

closeModal.addEventListener('click', () => {
    eventModal.style.display = 'none';
});

renderCalendar();
