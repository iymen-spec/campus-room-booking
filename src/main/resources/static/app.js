console.log("Campus Room Booking frontend loaded");

loadRooms();
loadBookings();

async function loadRooms() {
    const statusDiv = document.getElementById("rooms-status");
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
    const divRoomList = document.getElementById("rooms-list");
    divRoomList.replaceChildren();

    const ulRooms = document.createElement("ul");

    for (const room of rooms) {
        const li = document.createElement("li");
        li.textContent = `Room ${room.roomNumber} - ${room.building} - Capacity: ${room.capacity}`;
        ulRooms.appendChild(li);
    }

    divRoomList.appendChild(ulRooms);
}

async function loadBookings() {

    const statusDiv = document.getElementById("bookings-status");
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
    const divBookingsList = document.getElementById("bookings-list");

    divBookingsList.replaceChildren();

    const ulBookings = document.createElement("ul");

    for (const booking of bookings) {
        const li = document.createElement("li");

        li.append(
            `Booking ${booking.id} - Room ${booking.roomId} - ${booking.bookedBy}`,
            document.createElement("br"),
            `${booking.date}, ${booking.startTime} to ${booking.endTime}`,
            document.createElement("br"),
            `Status: ${booking.status}`
        );

        ulBookings.appendChild(li);
    }

    divBookingsList.appendChild(ulBookings);
}