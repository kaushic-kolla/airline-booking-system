
const searchForm = document.getElementById("searchForm");
const originInput = document.getElementById("origin");
const destinationInput =
    document.getElementById("destination");

const flightResults =
    document.getElementById("flightResults");

const statusBox = document.getElementById("status");
const searchButton = document.getElementById("searchButton");
const allFlightsButton =
    document.getElementById("allFlightsButton");

// Convert a timestamp to readable date and time
function formatDate(value) {
    if (!value) return "Not available";

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
        return "Invalid date";
    }

    return date.toLocaleString("en-US", {
        dateStyle: "medium",
        timeStyle: "short"
    });
}

// Display ticket price in USD
function formatPrice(price) {
    return new Intl.NumberFormat("en-US", {
        style: "currency",
        currency: "USD"
    }).format(price);
}

// Create a simple text element safely
function createText(tag, className, value) {
    const element = document.createElement(tag);
    element.className = className;
    element.textContent = value;
    return element;
}

// Display one flight from PostgreSQL
function displayFlight(flight) {

    const column = document.createElement("div");
    column.className = "col-md-6 col-lg-4";

    const card = document.createElement("article");
    card.className = "flight-card";

    card.appendChild(
        createText(
            "span",
            "badge text-bg-primary mb-3",
            flight.flightNumber
        )
    );

    card.appendChild(
        createText(
            "div",
            "flight-route mb-3",
            `${flight.origin} → ${flight.destination}`
        )
    );

    card.appendChild(
        createText(
            "p",
            "mb-2",
            `Departure: ${formatDate(flight.departureTime)}`
        )
    );

    card.appendChild(
        createText(
            "p",
            "mb-2",
            `Arrival: ${formatDate(flight.arrivalTime)}`
        )
    );

    card.appendChild(
        createText(
            "p",
            "mb-2",
            `Available seats: ${flight.availableSeats}`
        )
    );

    card.appendChild(
        createText(
            "div",
            "flight-price mt-3",
            formatPrice(flight.priceUsd)
        )
    );

    column.appendChild(card);
    flightResults.appendChild(column);
}

// Fetch flights from the Spring Boot API
async function loadFlights(url) {

    statusBox.textContent = "Loading flights...";
    searchButton.disabled = true;
    allFlightsButton.disabled = true;

    try {
        const response = await fetch(url);

        if (!response.ok) {
            throw new Error(
                `Server returned HTTP ${response.status}`
            );
        }

        const flights = await response.json();

        if (!Array.isArray(flights)) {
            throw new Error("Unexpected API response");
        }

        flightResults.replaceChildren();

        if (flights.length === 0) {
            statusBox.textContent =
                "No flights found for this search.";
            return;
        }

        statusBox.textContent =
            `${flights.length} flight(s) found`;

        flights.forEach(displayFlight);

    } catch (error) {
        flightResults.replaceChildren();

        statusBox.textContent =
            "Unable to load flights: " + error.message;

        console.error("Flight API error:", error);

    } finally {
        searchButton.disabled = false;
        allFlightsButton.disabled = false;
    }
}

// Search button
searchForm.addEventListener("submit", function (event) {

    event.preventDefault();

    const origin =
        originInput.value.trim().toUpperCase();

    const destination =
        destinationInput.value.trim().toUpperCase();

    if (!/^[A-Z]{3}$/.test(origin) ||
        !/^[A-Z]{3}$/.test(destination) ||
        origin === destination) {

        statusBox.textContent =
            "Enter two different 3-letter airport codes.";
        return;
    }

    const params = new URLSearchParams({
        origin: origin,
        destination: destination
    });

    loadFlights("/api/flights/search?" + params.toString());
});

// Show all flights button
allFlightsButton.addEventListener("click", function () {
    loadFlights("/api/flights");
});

// Automatically load flights when the page opens
loadFlights("/api/flights");
