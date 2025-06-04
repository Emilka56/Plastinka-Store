
const csrfToken = document.getElementById("csrfToken")?.value;
// Функция для обновления количества товаров в header
function updateCartQuantity() {
    fetch('/cart/total-quantity')
        .then(response => response.json())
        .then(data => {
            const cartCountElement = document.getElementById('cart-item-count');
            if (cartCountElement) {
                cartCountElement.textContent = data.quantity > 0 ? `(${data.quantity})` : '';
            }
        })
        .catch(error => console.error('Error updating cart quantity:', error));
}

// Функция для добавления товара в корзину
function addToCart(productId) {
    fetch('/cart/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `productId=${productId}`
    })
    .then(response => response.json())
    .then(data => {
        // Сразу обновляем количество в header
        updateCartQuantity();
    })
    .catch(error => console.error('Error adding to cart:', error));
}

function incrementQuantity(orderId, productId, button) {
    const input = button.previousElementSibling;
    const newValue = parseInt(input.value) + 1;
    input.value = newValue;
    updateQuantity(orderId, productId, newValue);
}

function decrementQuantity(orderId, productId, button) {
    const input = button.nextElementSibling;
    const newValue = Math.max(0, parseInt(input.value) - 1);
    input.value = newValue;
    updateQuantity(orderId, productId, newValue);
}

function updateQuantity(orderId, productId, quantity) {
    // Сохраняем предыдущее значение
    const quantityInput = document.querySelector(`[data-product-id="${productId}"] .quantity-controls input`);
    const previousValue = quantityInput ? quantityInput.value : 0;
    
    // Проверяем, что quantity является числом и не меньше 0
    quantity = Math.max(0, parseInt(quantity) || 0);
    
    const formData = new FormData();
    formData.append('orderId', orderId);
    formData.append('productId', productId);
    formData.append('quantity', quantity);

    fetch('/cart/update', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(data => {
                throw new Error(data.message || 'Ошибка при обновлении количества товара');
            });
        }
        return response.json();
    })
    .then(data => {
        if (data.status === 'success') {
            // Обновляем количество в header
            updateCartQuantity();
            
            // Обновляем отображение количества в карточке товара
            if (quantityInput) {
                quantityInput.value = quantity;
            }

            // Если количество 0, удаляем карточку товара
            if (quantity <= 0) {
                const productCard = document.querySelector(`[data-product-id="${productId}"]`);
                if (productCard) {
                    productCard.remove();
                    
                    // Если корзина пуста, показываем сообщение
                    const cartItems = document.querySelectorAll('.cart-item');
                    if (cartItems.length === 0) {
                        const cartContainer = document.querySelector('.cart-items');
                        if (cartContainer) {
                            cartContainer.innerHTML = `
                                <div class="empty-cart">
                                    <p>Ваша корзина пуста</p>
                                    <a href="/" class="continue-shopping">Продолжить покупки</a>
                                </div>
                            `;
                        }
                    }
                }
            }

            // Обновляем общую стоимость
            updateTotalPrice();
        } else {
            throw new Error('Ошибка при обновлении количества товара');
        }
    })
    .catch(error => {
        console.error('Error updating quantity:', error);
        alert(error.message);
        
        // Восстанавливаем предыдущее значение в поле ввода
        if (quantityInput) {
            quantityInput.value = previousValue;
            // Также обновляем общую стоимость после восстановления значения
            updateTotalPrice();
        }
    });
}

// Функция для извлечения числа из строки цены
function extractPrice(priceText) {
    // Удаляем все пробелы и заменяем запятую на точку
    const cleanText = priceText.replace(/\s+/g, '').replace(',', '.');
    // Извлекаем число с помощью регулярного выражения
    const match = cleanText.match(/(\d+\.?\d*)/);
    return match ? parseFloat(match[1]) : 0;
}

// Функция для пересчета общей стоимости
function updateTotalPrice() {
    const cartItems = document.querySelectorAll('.cart-item');
    let totalPrice = 0;

    cartItems.forEach(item => {
        // Получаем текст цены и очищаем его
        const priceText = item.querySelector('.item-price').textContent;
        const price = extractPrice(priceText);
        
        // Получаем количество
        const quantityInput = item.querySelector('.quantity-controls input');
        const quantity = parseInt(quantityInput.value) || 0;
        
        // Вычисляем стоимость товара
        const itemPrice = price * quantity;
        
        // Добавляем к общей сумме
        totalPrice += itemPrice;
        
        console.log({
            priceText,
            price,
            quantity,
            itemPrice,
            currentTotal: totalPrice
        });
    });

    // Округляем общую сумму до двух знаков после запятой
    const roundedTotal = Math.round(totalPrice * 100) / 100;
    const formattedTotal = roundedTotal.toFixed(2);
    
    console.log('Итоговая сумма:', {
        totalPrice,
        roundedTotal,
        formattedTotal
    });

    // Обновляем отображение суммы везде
    const elements = [
        document.querySelector('.total-price span:last-child'),
        document.querySelector('.total-amount')
    ];

    elements.forEach(element => {
        if (element) {
            element.textContent = `${formattedTotal} руб`;
        }
    });
}

// Обновляем количество при загрузке любой страницы
document.addEventListener('DOMContentLoaded', function() {
    updateCartQuantity();
});

function showOrderModal() {
    const modal = document.getElementById('orderModal');
    modal.classList.add('show');

    updateTotalPrice();

    const deliveryDate = new Date();
    deliveryDate.setDate(deliveryDate.getDate() + 7);
    

    const year = deliveryDate.getFullYear();
    const month = String(deliveryDate.getMonth() + 1).padStart(2, '0');
    const day = String(deliveryDate.getDate()).padStart(2, '0');
    const dateForServer = `${year}-${month}-${day}T00:00:00`;
    
    // Отправляем запрос для форматирования даты через DateConverter
    fetch(`/cart/format-date?date=${dateForServer}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text();
        })
        .then(formattedDate => {
            const deliveryDateElement = document.querySelector('.delivery-date');
            if (deliveryDateElement) {
                deliveryDateElement.textContent = formattedDate;
            }
        })
        .catch(error => {
            console.error('Error formatting date:', error);
            // В случае ошибки форматируем на клиенте
            const fallbackDate = `${day}-${month}-${year}`;
            
            const deliveryDateElement = document.querySelector('.delivery-date');
            if (deliveryDateElement) {
                deliveryDateElement.textContent = fallbackDate;
            }
        });
}

function closeOrderModal() {
    const modal = document.getElementById('orderModal');
    modal.classList.remove('show');
}

function submitOrder(event) {
    event.preventDefault();
    
    const form = document.getElementById('orderForm');
    const orderId = form.querySelector('[name="orderId"]').value;
    const city = form.querySelector('[name="city"]').value;
    const street = form.querySelector('[name="street"]').value;
    const houseNumber = form.querySelector('[name="house"]').value;
    const apartmentNumber = form.querySelector('[name="apartment"]').value;
    const deliveryDate = form.querySelector('[name="deliveryDate"]').value;
    const promoCode = form.querySelector('[name="promoCode"]').value;

    // Преобразуем дату в нужный формат
    const formattedDate = deliveryDate.replace('T', ' ');
    
    const formData = new FormData();
    formData.append('orderId', orderId);
    formData.append('city', city);
    formData.append('street', street);
    formData.append('houseNumber', houseNumber);
    formData.append('apartmentNumber', apartmentNumber);
    formData.append('deliveryDateStr', formattedDate);
    if (promoCode) {
        formData.append('promoCode', promoCode);
    }

    fetch('/cart/complete-order', {
        method: 'POST',
        headers: {'X-CSRF-TOKEN': csrfToken},
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(data => {
                throw new Error(data.message || 'Ошибка при оформлении заказа');
            });
        }
        return response.json();
    })
    .then(data => {
        if (data.status === 'success') {
            alert('Заказ успешно оформлен!');
            window.location.href = '/profile'; // Перенаправляем на страницу профиля
        } else {
            throw new Error('Ошибка при оформлении заказа');
        }
    })
    .catch(error => {
        console.error('Error completing order:', error);
        alert('Произошла ошибка при оформлении заказа: ' + error.message);
    });
}

// Закрытие модального окна при клике вне его
window.onclick = function(event) {
    const modal = document.getElementById('orderModal');
    if (event.target === modal) {
        closeOrderModal();
    }
}

// Функция для проверки промокода и пересчета суммы
function checkPromoCode() {
    const promoCode = document.querySelector('#promoCode').value;
    if (!promoCode) return;

    const totalPriceElement = document.querySelector('.total-amount');
    const originalPrice = extractPrice(totalPriceElement.textContent);

    fetch('/discounts/apply-promo', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `promoCode=${encodeURIComponent(promoCode)}&originalPrice=${originalPrice}`
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(data => {
                throw new Error(data.message || 'Промокод недействителен');
            });
        }
        return response.json();
    })
    .then(data => {
        if (data.status === 'success') {
            // Обновляем отображение цены
            const elements = [
                document.querySelector('.total-price span:last-child'),
                document.querySelector('.total-amount')
            ];

            elements.forEach(element => {
                if (element) {
                    // Показываем старую цену
                    const oldPriceElement = document.createElement('span');
                    oldPriceElement.className = 'old-price';
                    oldPriceElement.style.textDecoration = 'line-through';
                    oldPriceElement.style.color = '#999';
                    oldPriceElement.style.marginRight = '10px';
                    oldPriceElement.textContent = `${originalPrice.toFixed(2)} руб`;

                    // Показываем новую цену
                    element.innerHTML = '';
                    element.appendChild(oldPriceElement);
                    element.appendChild(document.createTextNode(`${data.discountedPrice.toFixed(2)} руб`));
                }
            });

            // Показываем сообщение об успехе
            const messageElement = document.querySelector('#promoMessage') || document.createElement('div');
            messageElement.id = 'promoMessage';
            messageElement.className = 'text-success';
            messageElement.style.marginTop = '5px';
            messageElement.textContent = 'Промокод успешно применен!';
            document.querySelector('#promoCode').parentNode.appendChild(messageElement);
        }
    })
    .catch(error => {
        console.error('Error applying promo code:', error);
        // Показываем сообщение об ошибке
        const messageElement = document.querySelector('#promoMessage') || document.createElement('div');
        messageElement.id = 'promoMessage';
        messageElement.className = 'text-danger';
        messageElement.style.marginTop = '5px';
        messageElement.textContent = error.message;
        document.querySelector('#promoCode').parentNode.appendChild(messageElement);
    });
}

// Добавляем обработчик события для поля промокода
document.addEventListener('DOMContentLoaded', function() {
    const promoCodeInput = document.querySelector('#promoCode');
    if (promoCodeInput) {
        promoCodeInput.addEventListener('change', checkPromoCode);
    }
}); 