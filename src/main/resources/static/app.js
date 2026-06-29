console.log("Campus Room Booking frontend loaded");

function byId(id) {
    return document.getElementById(id);
}

function createEl(el) {
    return document.createElement(el);
}

loadRooms();
loadBookings();

const availabilityForm = byId("availability-form");

availabilityForm.addEventListener("submit", handleAvailabilitySearch);

async function handleAvailabilitySearch(event) {
    event.preventDefault();

    const date = byId("availability-date").value.trim();
    const startTime = byId("availability-start-time").value.trim();
    const endTime = byId("availability-end-time").value.trim();
    const building = byId("availability-building").value.trim();
    const minCapacity = byId("availability-min-capacity").value.trim();

    const params = new URLSearchParams();

    params.append("date", date);
    params.append("startTime", startTime);
    params.append("endTime", endTime);

    if (building !== "") {
        params.append("building", building);
    }

    if (minCapacity !== "") {
        params.append("minCapacity", minCapacity);
    }

    const url = "/api/rooms/available?" + params.toString();

    const statusDiv = byId("availability-status");

    const availableRoomsList = byId("available-rooms-list");
    availableRoomsList.replaceChildren();

    try {
        statusDiv.textContent = "Searching available rooms...";

        const response = await fetch(url);

        const data = await response.json();
        if (!response.ok) {
            statusDiv.textContent = data.message || "Could not search available rooms.";
            return;
        }

        const availableRooms = data;

        if (availableRooms.length === 0) {
            statusDiv.textContent = "No available rooms";
            return;
        }

        statusDiv.textContent = "";
        renderAvailableRooms(availableRooms);

    } catch (error) {
        statusDiv.textContent = "Could not search available rooms.";
        console.error("Failed to search for available rooms:", error);
    }
}

function renderAvailableRooms(availableRooms) {
    const availableRoomsList = byId("available-rooms-list");
    availableRoomsList.replaceChildren();

    const ul = createEl("ul");

    for (const room of availableRooms) {
        const li = createEl("li");
        li.textContent = `Room ${room.roomNumber} - ${room.building} - Capacity: ${room.capacity} `;

        const button = createEl("button");
        button.type = "button";
        button.addEventListener("click", () => {
            createBooking(room.id);
        });

        button.textContent = "Book this room";
        li.appendChild(button);
        ul.appendChild(li);
    }

    availableRoomsList.appendChild(ul);
}

async function createBooking(roomId) {
    const bookedBy = byId("booked-by").value.trim();
    const date = byId("availability-date").value.trim();
    const startTime = byId("availability-start-time").value.trim();
    const endTime = byId("availability-end-time").value.trim();

    const bookingRequest = {
        roomId,
        bookedBy,
        date,
        startTime,
        endTime
    };

    const statusDiv = byId("availability-status");
    statusDiv.textContent = "Creating booking...";

    try {
        const response = await fetch("/api/bookings", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(bookingRequest)
        });

        const data = await response.json();
        if (!response.ok) {
            statusDiv.textContent = data.message || "Could not create booking.";
            return;
        }

        loadBookings();
        byId("available-rooms-list").replaceChildren();
        statusDiv.textContent = "Booking created. Search again to see updated availability.";

    } catch (error) {
        statusDiv.textContent = "Could not create booking.";
        console.error("Failed to create booking:", error);
    }
}

async function loadRooms() {
    const statusDiv = byId("rooms-status");
    statusDiv.textContent = "Loading rooms...";

    try {
        const response = await fetch("/api/rooms");

        if (!response.ok) {
            statusDiv.textContent = "Could not load rooms.";
            return;
        }

        const rooms = await response.json();

        if (rooms.length === 0) {
            statusDiv.textContent = "No rooms found";
            return;
        }

        statusDiv.textContent = "";
        renderRooms(rooms);

    } catch (error) {
        statusDiv.textContent = "Could not load rooms.";
        console.error("Failed to load rooms:", error);
    }
}

function renderRooms(rooms) {
    const divRoomList = byId("rooms-list");
    divRoomList.replaceChildren();

    const ulRooms = createEl("ul");

    for (const room of rooms) {
        const li = createEl("li");
        li.textContent = `Room ${room.roomNumber} - ${room.building} - Capacity: ${room.capacity}`;
        ulRooms.appendChild(li);
    }

    divRoomList.appendChild(ulRooms);
}

async function loadBookings() {
    const statusDiv = byId("bookings-status");
    statusDiv.textContent = "Loading bookings...";

    try {
        const response = await fetch("/api/bookings");

        if (!response.ok) {
            statusDiv.textContent = "Could not load bookings.";
            return;
        }

        const bookings = await response.json();

        if (bookings.length === 0) {
            statusDiv.textContent = "No bookings found";
            return;
        }

        statusDiv.textContent = "";
        renderBookings(bookings);

    } catch (error) {
        statusDiv.textContent = "Could not load bookings.";
        console.error("Failed to load bookings:", error);
    }
}

function renderBookings(bookings) {
    const divBookingsList = byId("bookings-list");

    divBookingsList.replaceChildren();

    const ulBookings = createEl("ul");

    for (const booking of bookings) {
        const li = createEl("li");

        li.append(
            `Booking ${booking.id} - Room ${booking.roomId} - ${booking.bookedBy}`,
            createEl("br"),
            `${booking.date}, ${booking.startTime} to ${booking.endTime}`,
            createEl("br"),
            `Status: ${booking.status}`
        );

        ulBookings.appendChild(li);
    }

    divBookingsList.appendChild(ulBookings);
}