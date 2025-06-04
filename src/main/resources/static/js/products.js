function showLoading() {
    document.getElementById('loadingSpinner').style.display = 'block';
}

function hideLoading() {
    document.getElementById('loadingSpinner').style.display = 'none';
}

function escapeHtml(unsafe) {
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

// Функция для плавной прокрутки к секции жанра
document.addEventListener('DOMContentLoaded', function() {
    // Обработка хэша при загрузке страницы
    if (window.location.hash) {
        const targetSection = document.querySelector(window.location.hash);
        if (targetSection) {
            setTimeout(() => {
                targetSection.scrollIntoView({ behavior: 'smooth' });
            }, 100);
        }
    }
});

function loadMoreProducts(genre, sectionId) {
    showLoading();
    
    const safeGenre = genre.replace(/[<>"'&]/g, '');
    
    fetch('/mainPage/load', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-CSRF-TOKEN': csrfToken
        },
        body: 'genre=' + encodeURIComponent(safeGenre) + '&count=5'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(products => {
        hideLoading();
        if (products && products.length > 0) {
            const container = document.querySelector('#' + sectionId + ' .product-container');
            products.forEach(product => {
                const card = document.createElement('div');
                card.className = 'product-card';
                card.onclick = () => window.location.href = '/product/' + product.id;
                
                const imageUrl = product.images && product.images.length > 0 
                    ? '/files/' + escapeHtml(product.images[0].storageName)
                    : '/images/default-vinyl.jpg';
                
                card.innerHTML = `
                    <img src="${imageUrl}" alt="${escapeHtml(product.album)}" class="product-img">
                    <p class="artist">${escapeHtml(product.artist)}</p>
                    <p class="album">${escapeHtml(product.album)}</p>
                    <p class="price">${escapeHtml(String(product.price))} руб</p>
                    <button class="add-to-cart-btn" onclick="event.stopPropagation(); addToCart(${product.id})">
                        В корзину
                    </button>
                `;
                container.appendChild(card);
            });
        } else {
            alert('Больше пластинок не найдено');
        }
    })
    .catch(error => {
        hideLoading();
        console.error('Ошибка:', error);
        alert('Произошла ошибка при загрузке пластинок');
    });
} 