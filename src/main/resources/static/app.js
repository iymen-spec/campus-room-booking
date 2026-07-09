console.log("Campus Room Booking frontend loaded");

function byId(id) {
    return document.getElementById(id);
}

function createEl(el) {
    return document.createElement(el);
}

function setText(id, message) {
    byId(id).textContent = message;
}

function clearElement(id) {
    byId(id).replaceChildren();
}

function setRoomsStatus(message) {
    setText("rooms-status", message);
}

function setAvailabilityStatus(message) {
    setText("availability-status", message);
}

function setBookingsStatus(message) {
    setText("bookings-status", message);
}

function clearAvailableRooms() {
    clearElement("available-rooms-list");
}

function createLabeledInput(labelText, input) {
    const label = createEl("label");
    label.append(labelText, input);
    return label;
}

function createInput(type, value) {
    const input = createEl("input");
    input.type = type;
    input.value = value;
    return input;
}

function normalizeTimeForInput(time) {
    if (!time) {
        return "";
    }

    return time.slice(0, 5);
}

async function readJsonSafely(response) {
    try {
        return await response.json();
    } catch (error) {
        return null;
    }
}

async function fetchJson(url, options, fallbackMessage) {
    const response = await fetch(url, options);
    const data = await readJsonSafely(response);

    if (!response.ok) {
        throw new Error(data?.message || fallbackMessage);
    }

    return data;
}

async function fetchWithoutBody(url, options, fallbackMessage) {
    const response = await fetch(url, options);

    if (!response.ok) {
        const data = await readJsonSafely(response);
        throw new Error(data?.message || fallbackMessage);
    }
}

loadRooms();
loadBookings();

const availabilityForm = byId("availability-form");
availabilityForm.addEventListener("submit", handleAvailabilitySearch);

async function handleAvailabilitySearch(event) {
    event.preventDefault();

    const searchValues = readAvailabilitySearchValues();
    const url = buildAvailabilitySearchUrl(searchValues);

    clearAvailableRooms();
    setAvailabilityStatus("Searching available rooms...");

    try {
        const availableRooms = await sendAvailabilitySearchRequest(url);

        if (availableRooms.length === 0) {
            setAvailabilityStatus("No available rooms");
            return;
        }

        setAvailabilityStatus("");
        renderAvailableRooms(availableRooms);
    } catch (error) {
        setAvailabilityStatus(error.message || "Could not search available rooms.");
        console.error("Failed to search for available rooms:", error);
    }
}

function readAvailabilitySearchValues() {
    return {
        date: byId("availability-date").value.trim(),
        startTime: byId("availability-start-time").value.trim(),
        endTime: byId("availability-end-time").value.trim(),
        building: byId("availability-building").value.trim(),
        minCapacity: byId("availability-min-capacity").value.trim()
    };
}

function buildAvailabilitySearchUrl(searchValues) {
    const params = new URLSearchParams();

    params.append("date", searchValues.date);
    params.append("startTime", searchValues.startTime);
    params.append("endTime", searchValues.endTime);

    if (searchValues.building !== "") {
        params.append("building", searchValues.building);
    }

    if (searchValues.minCapacity !== "") {
        params.append("minCapacity", searchValues.minCapacity);
    }

    return "/api/rooms/available?" + params.toString();
}

async function sendAvailabilitySearchRequest(url) {
    return fetchJson(url, {}, "Could not search available rooms.");
}

function renderAvailableRooms(availableRooms) {
    const availableRoomsList = byId("available-rooms-list");
    availableRoomsList.replaceChildren();

    const ul = createEl("ul");

    for (const room of availableRooms) {
        ul.appendChild(buildAvailableRoomItem(room));
    }

    availableRoomsList.appendChild(ul);
}

function buildAvailableRoomItem(room) {
    const li = createEl("li");
    li.textContent = `Room ${room.roomNumber} - ${room.building} - Capacity: ${room.capacity} `;

    const bookButton = createEl("button");
    bookButton.type = "button";
    bookButton.textContent = "Book this room";
    bookButton.addEventListener("click", () => {
        createBooking(room.id);
    });

    li.appendChild(bookButton);

    return li;
}

function readBookingFormValues() {
    return {
        bookedBy: byId("booked-by").value.trim(),
        date: byId("availability-date").value.trim(),
        startTime: byId("availability-start-time").value.trim(),
        endTime: byId("availability-end-time").value.trim()
    };
}

function buildBookingRequest(roomId, formValues) {
    return {
        roomId,
        bookedBy: formValues.bookedBy,
        date: formValues.date,
        startTime: formValues.startTime,
        endTime: formValues.endTime
    };
}

async function sendCreateBookingRequest(bookingRequest) {
    return fetchJson(
        "/api/bookings",
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(bookingRequest)
        },
        "Could not create booking."
    );
}

async function createBooking(roomId) {
    const formValues = readBookingFormValues();
    const bookingRequest = buildBookingRequest(roomId, formValues);

    setAvailabilityStatus("Creating booking...");

    try {
        await sendCreateBookingRequest(bookingRequest);

        await loadBookings();
        clearAvailableRooms();
        setAvailabilityStatus("Booking created. Search again to see updated availability.");
    } catch (error) {
        setAvailabilityStatus(error.message || "Could not create booking.");
        console.error("Failed to create booking:", error);
    }
}

async function loadRooms() {
    setRoomsStatus("Loading rooms...");

    try {
        const rooms = await sendLoadRoomsRequest();

        renderRooms(rooms);

        if (rooms.length === 0) {
            setRoomsStatus("No rooms found");
            return;
        }

        setRoomsStatus("");
    } catch (error) {
        setRoomsStatus(error.message || "Could not load rooms.");
        console.error("Failed to load rooms:", error);
    }
}

async function sendLoadRoomsRequest() {
    return fetchJson("/api/rooms", {}, "Could not load rooms.");
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
    setBookingsStatus("Loading bookings...");

    try {
        const bookings = await sendLoadBookingsRequest();

        renderBookings(bookings);

        if (bookings.length === 0) {
            setBookingsStatus("No bookings found");
            return;
        }

        setBookingsStatus("");
    } catch (error) {
        setBookingsStatus(error.message || "Could not load bookings.");
        console.error("Failed to load bookings:", error);
    }
}

async function sendLoadBookingsRequest() {
    return fetchJson("/api/bookings", {}, "Could not load bookings.");
}

function renderBookings(bookings) {
    const divBookingsList = byId("bookings-list");
    divBookingsList.replaceChildren();

    const ulBookings = createEl("ul");

    for (const booking of bookings) {
        ulBookings.appendChild(buildBookingItem(booking));
    }

    divBookingsList.appendChild(ulBookings);
}

function buildBookingItem(booking) {
    const li = createEl("li");

    li.append(
        `Booking ${booking.id} - Room ${booking.roomId} - ${booking.bookedBy}`,
        createEl("br"),
        `${booking.date}, ${booking.startTime} to ${booking.endTime}`,
        createEl("br"),
        `Status: ${booking.status}`
    );

    if (booking.status === "ACTIVE") {
        addBookingManagementControls(li, booking);
    }

    return li;
}

function addBookingManagementControls(li, booking) {
    const cancelButton = buildCancelButton(booking);
    const rescheduleButton = buildShowRescheduleButton(booking);

    li.append(
        createEl("br"),
        cancelButton,
        " ",
        rescheduleButton
    );
}

function buildCancelButton(booking) {
    const cancelButton = createEl("button");
    cancelButton.type = "button";
    cancelButton.textContent = "Cancel";
    cancelButton.addEventListener("click", () => {
        cancelBooking(booking.id);
    });

    return cancelButton;
}

function buildShowRescheduleButton(booking) {
    const rescheduleButton = createEl("button");
    const rescheduleFormContainer = createEl("div");

    rescheduleButton.type = "button";
    rescheduleButton.textContent = "Reschedule";
    rescheduleButton.addEventListener("click", () => {
        rescheduleFormContainer.replaceChildren(buildRescheduleForm(booking));
    });

    const wrapper = createEl("div");
    wrapper.append(rescheduleButton, rescheduleFormContainer);

    return wrapper;
}

function buildRescheduleForm(booking) {
    const form = createEl("form");
    const inputs = buildRescheduleInputs(booking);

    const saveButton = createEl("button");
    saveButton.type = "submit";
    saveButton.textContent = "Save Reschedule";

    form.addEventListener("submit", (event) => {
        event.preventDefault();

        const formValues = readRescheduleFormValues(inputs);
        const requestBody = buildRescheduleRequest(formValues);

        rescheduleBooking(booking.id, requestBody);
    });

    form.append(
        createEl("br"),
        "Reschedule: ",
        createLabeledInput("Room: ", inputs.roomIdInput),
        " ",
        createLabeledInput("Date: ", inputs.dateInput),
        " ",
        createLabeledInput("Start: ", inputs.startTimeInput),
        " ",
        createLabeledInput("End: ", inputs.endTimeInput),
        " ",
        saveButton
    );

    return form;
}

function buildRescheduleInputs(booking) {
    return {
        roomIdInput: createInput("number", booking.roomId),
        dateInput: createInput("date", booking.date),
        startTimeInput: createInput("time", normalizeTimeForInput(booking.startTime)),
        endTimeInput: createInput("time", normalizeTimeForInput(booking.endTime))
    };
}

function readRescheduleFormValues(inputs) {
    return {
        roomId: inputs.roomIdInput.value.trim(),
        date: inputs.dateInput.value.trim(),
        startTime: inputs.startTimeInput.value.trim(),
        endTime: inputs.endTimeInput.value.trim()
    };
}

function buildRescheduleRequest(formValues) {
    return {
        roomId: Number(formValues.roomId),
        date: formValues.date,
        startTime: formValues.startTime,
        endTime: formValues.endTime
    };
}

async function sendRescheduleBookingRequest(id, requestBody) {
    return fetchJson(
        `/api/bookings/${id}/reschedule`,
        {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestBody)
        },
        "Could not reschedule booking."
    );
}

async function rescheduleBooking(id, requestBody) {
    setBookingsStatus("Rescheduling booking...");

    try {
        await sendRescheduleBookingRequest(id, requestBody);

        await loadBookings();
        clearAvailableRooms();
        setBookingsStatus("Booking rescheduled.");
    } catch (error) {
        setBookingsStatus(error.message || "Could not reschedule booking.");
        console.error("Failed to reschedule booking:", error);
    }
}

async function sendCancelBookingRequest(id) {
    return fetchWithoutBody(
        `/api/bookings/${id}`,
        {
            method: "DELETE"
        },
        "Could not cancel booking."
    );
}

async function cancelBooking(id) {
    setBookingsStatus("Canceling booking...");

    try {
        await sendCancelBookingRequest(id);

        await loadBookings();
        clearAvailableRooms();
        setBookingsStatus("Booking canceled.");
    } catch (error) {
        setBookingsStatus(error.message || "Could not cancel booking.");
        console.error("Failed to cancel booking:", error);
    }
}
