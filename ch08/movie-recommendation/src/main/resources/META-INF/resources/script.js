// script.js
document.getElementById('searchButton').addEventListener('click', function() {
    const query = document.getElementById('searchInput').value;
    if (query) {
        searchMovies(query);
    }
});

function searchMovies(query) {
    fetch(`/movies/api/search?q=${encodeURIComponent(query)}`)
        .then(response => response.json())
        .then(data => {
            displayResults(data);
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function displayResults(movies) {
    const resultsContainer = document.getElementById('results');
    resultsContainer.innerHTML = ''; // Clear previous results

    if (movies.length === 0) {
        resultsContainer.innerHTML = '<p>No movies found.</p>';
        return;
    }

    movies.forEach(movie => {
        const movieElement = document.createElement('div');
        movieElement.className = 'movie';

        movieElement.innerHTML = `
            <img src="${movie.poster}" alt="${movie.name}">
            <div class="movie-info">
                <h2>${movie.name}</h2>
                <p>${movie.plot}</p>
                <p><strong>Rating:</strong> ${movie.rating}</p>
                <p><strong>Director:</strong> ${movie.director}</p>
            </div>
        `;

        resultsContainer.appendChild(movieElement);
    });
}