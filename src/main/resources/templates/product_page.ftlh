<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${product.artist} - ${product.album}</title>
    <link rel="stylesheet" href="/styles/global.css">
    <link rel="stylesheet" href="/styles/productPage.css">
    <link rel="stylesheet" href="/styles/reviews.css">
    <link rel="stylesheet" href="/styles/font.css">
    <script src="/js/cart.js"></script>
    <script src="/js/reviews.js"></script>
</head>
<body>
    <#include "header.ftlh">
    <input type="hidden" id="csrfToken" name="${_csrf.parameterName}" value="${_csrf.token}">
    
    <main class="product-container">
        <div class="product-details">
            <div class="product-image-container">
                <#if product.images?? && product.images?has_content>
                    <img src="/files/${product.images[0].storageName}" alt="${product.album}" class="product-image">
                </#if>
            </div>
            <div class="product-info">
                <h1 class="artist">${product.artist}</h1>
                <h2 class="album">${product.album}</h2>
                <p class="genre">Жанр: ${product.genre}</p>
                <p class="price">${product.price?string["0.00"]} руб</p>
                
                <#if product.previewUrl?? && product.previewUrl != "">
                    <div class="audio-preview">
                        <h3>Прослушать фрагмент:</h3>
                        <audio controls>
                            <source src="${product.previewUrl}" type="audio/mpeg">
                            Ваш браузер не поддерживает аудио-элемент.
                        </audio>
                    </div>
                </#if>
                
                <button class="add-to-cart" onclick="addToCart(${product.id})">
                    Добавить в корзину
                </button>
            </div>
        </div>

        <section class="reviews-section">
            <div class="reviews-header">
                <h2>Отзывы</h2>
            </div>

            <#if user??>
                <#if !hasUserReviewed>
                    <form class="review-form" onsubmit="submitReview(event)">
                        <input type="hidden" name="productId" value="${product.id}">
                        <input type="hidden" name="rating" id="review-rating" value="0">
                        <div class="rating-stars">
                            <span>Ваша оценка:</span>
                            <div class="stars-container">
                                <#list 1..5 as i>
                                    <span class="star" data-rating="${i}" onclick="setRating(${i})">★</span>
                                </#list>
                            </div>
                        </div>
                        <textarea name="text" placeholder="Напишите ваш отзыв..." required></textarea>
                        <button type="submit">Отправить отзыв</button>
                    </form>
                </#if>
            <#else>
                <p class="login-prompt">Чтобы оставить отзыв, пожалуйста, <a href="/login">войдите</a> в систему</p>
            </#if>

            <div class="reviews-container">
                <#if reviews?? && reviews?has_content>
                    <#list reviews as review>
                        <div class="review" data-review-id="${review.id}">
                            <div class="review-header">
                                <span class="user-name">${review.user.firstName} ${review.user.lastName}</span>
                                <span class="review-date">${review.formattedCreatedAt}</span>
                                <span class="review-rating">
                                    <#list 1..5 as i>
                                        <span class="star ${((review.rating!'0')?number >= i?number)?then('filled', '')}">★</span>
                                    </#list>
                                </span>
                            </div>
                            <div class="review-text">${review.text}</div>
                            <div class="review-footer">
                                <button class="like-button <#if user?? && .vars['review_' + review.id + '_liked']?? && .vars['review_' + review.id + '_liked']>liked</#if>" 
                                        onclick="toggleLike(${review.id}, this)">
                                    <span class="like-icon">❤</span>
                                    <span class="likes-count">${review.likesCount}</span>
                                </button>
                            </div>
                        </div>
                    </#list>
                <#else>
                    <p class="no-reviews">Пока нет отзывов. Будьте первым!</p>
                </#if>
            </div>
        </section>
    </main>
</body>
</html>