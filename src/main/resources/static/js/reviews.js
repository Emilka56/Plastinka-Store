// Функция для добавления отзыва
function submitReview(event) {
    event.preventDefault();
    
    const form = event.target;
    const productId = form.querySelector('input[name="productId"]').value;
    const text = form.querySelector('textarea[name="text"]').value;
    const rating = form.querySelector('input[name="rating"]').value;
    
    if (!text.trim()) {
        alert('Пожалуйста, введите текст отзыва');
        return;
    }
    
    fetch('/reviews/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `productId=${encodeURIComponent(productId)}&text=${encodeURIComponent(text)}&rating=${encodeURIComponent(rating)}`
    })
    .then(response => response.json())
    .then(data => {
        if (data.status === 'success') {
            // Создаем новый элемент отзыва
            const reviewsContainer = document.querySelector('.reviews-container');
            const noReviewsMessage = reviewsContainer.querySelector('.no-reviews');
            if (noReviewsMessage) {
                noReviewsMessage.remove();
            }
            
            const reviewElement = createReviewElement(data);
            reviewsContainer.insertBefore(reviewElement, reviewsContainer.firstChild);
            
            // Очищаем форму
            form.reset();
            
            // Сбрасываем рейтинг
            setRating(0);
            
            // Скрываем форму, так как пользователь уже оставил отзыв
            form.style.display = 'none';
        } else {
            alert(data.message || 'Ошибка при добавлении отзыва');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Ошибка при добавлении отзыва');
    });
}

// Функция для создания элемента отзыва
function createReviewElement(data) {
    const reviewDiv = document.createElement('div');
    reviewDiv.className = 'review';
    reviewDiv.dataset.reviewId = data.reviewId;
    
    const stars = Array(5).fill('').map((_, i) => 
        `<span class="star ${i < data.rating ? 'filled' : ''}">★</span>`
    ).join('');
    
    reviewDiv.innerHTML = `
        <div class="review-header">
            <span class="user-name">${data.userName}</span>
            <span class="review-date">${formatDate(data.createdAt)}</span>
            <span class="review-rating">
                ${stars}
            </span>
        </div>
        <div class="review-text">${data.text}</div>
        <div class="review-footer">
            <button class="like-button" onclick="toggleLike(${data.reviewId}, this)">
                <span class="like-icon">❤</span>
                <span class="likes-count">${data.likesCount}</span>
            </button>
        </div>
    `;
    
    return reviewDiv;
}

// Функция для форматирования даты
function formatDate(dateStr) {
    const date = new Date(dateStr);
    return date.toLocaleDateString('ru-RU', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
    });
}

// Функция для обработки лайков
function toggleLike(reviewId, button) {
    const formData = new FormData();
    formData.append('reviewId', reviewId);
    
    fetch('/reviews/like', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.status === 'success') {
            // Обновляем количество лайков
            const likesCount = button.querySelector('.likes-count');
            likesCount.textContent = data.likesCount;
            
            // Обновляем стиль кнопки
            button.classList.toggle('liked', data.isLiked);

            // Получаем текущий отзыв и все отзывы
            const currentReview = button.closest('.review');
            const reviewsContainer = document.querySelector('.reviews-container');
            const allReviews = Array.from(reviewsContainer.querySelectorAll('.review'));

            // Находим правильную позицию для отзыва
            let insertBefore = null;
            for (let review of allReviews) {
                if (review === currentReview) continue;
                const reviewLikes = parseInt(review.querySelector('.likes-count').textContent);
                if (data.likesCount > reviewLikes) {
                    insertBefore = review;
                    break;
                }
            }

            // Перемещаем отзыв на новую позицию
            if (insertBefore) {
                reviewsContainer.insertBefore(currentReview, insertBefore);
            } else {
                reviewsContainer.appendChild(currentReview);
            }
        } else {
            alert(data.message || 'Ошибка при обработке лайка');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Ошибка при обработке лайка');
    });
}

function setRating(rating) {
    document.getElementById('review-rating').value = rating;
    const stars = document.querySelectorAll('.review-form .star');
    stars.forEach((star, index) => {
        if (index < rating) {
            star.classList.add('selected');
        } else {
            star.classList.remove('selected');
        }
    });
} 