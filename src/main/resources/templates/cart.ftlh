<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="/styles/global.css">
    <link rel="stylesheet" href="/styles/cart.css">
    <link rel="stylesheet" href="/styles/font.css">
    <script src="/js/cart.js"></script>
    <title>Корзина</title>
</head>
<body>
    <#include "header.ftlh">
    <input type="hidden" id="csrfToken" name="${_csrf.parameterName}" value="${_csrf.token}">
    
    <main class="cart-container">
        <h1>Корзина</h1>
        
        <#if cartItems?? && cartItems?has_content>
            <div class="cart-items">
                <#list cartItems as item>
                    <div class="cart-item" data-product-id="${item.product.id}">
                        <div class="item-image">
                            <#if item.product.images?? && item.product.images?has_content>
                                <img src="/files/${item.product.images[0].storageName}" alt="${item.product.album}">
                            </#if>
                        </div>
                        <div class="item-details">
                            <h3>${item.product.artist} - ${item.product.album}</h3>
                            <p class="item-price">${item.product.price?string["0.00"]} руб</p>
                            <div class="quantity-controls">
                                <button onclick="decrementQuantity(${orderId}, ${item.product.id}, this)">-</button>
                                <input type="number" min="0" value="${item.quantity}" 
                                    onchange="updateQuantity(${orderId}, ${item.product.id}, this.value)"
                                    onkeyup="if(event.key === 'Enter') updateQuantity(${orderId}, ${item.product.id}, this.value)">
                                <button onclick="incrementQuantity(${orderId}, ${item.product.id}, this)">+</button>
                            </div>
                            <button class="remove-button" onclick="updateQuantity(${orderId}, ${item.product.id}, 0)">Удалить</button>
                        </div>
                    </div>
                </#list>
                
                <div class="cart-summary">
                    <div class="total-price">
                        <span>Итого:</span>
                        <span>${totalPrice?string["0.00"]} руб</span>
                    </div>
                    <button class="checkout-button" onclick="showOrderModal()">Оформить заказ</button>
                </div>
            </div>
        <#else>
            <div class="empty-cart">
                <p>Ваша корзина пуста</p>
                <a href="/" class="continue-shopping">Продолжить покупки</a>
            </div>
        </#if>

        <div id="orderModal" class="modal">
            <div class="modal-content">
                <span class="close" onclick="closeOrderModal()">&times;</span>
                <h2>Оформление заказа</h2>
                <form id="orderForm" onsubmit="submitOrder(event)">

                    <input type="hidden" name="orderId" value="${orderId!}">
                    <div class="form-group">
                        <label for="city">Город:</label>
                        <input type="text" id="city" name="city" required placeholder="Введите город">
                    </div>
                    <div class="form-group">
                        <label for="street">Улица:</label>
                        <input type="text" id="street" name="street" required placeholder="Введите улицу">
                    </div>
                    <div class="form-group">
                        <label for="house">Номер дома:</label>
                        <input type="number" id="house" name="house" required placeholder="Введите номер дома">
                    </div>
                    <div class="form-group">
                        <label for="apartment">Номер квартиры:</label>
                        <input type="number" id="apartment" name="apartment" required placeholder="Введите номер квартиры">
                    </div>
                    <div class="form-group">
                        <label for="promoCode">Промокод:</label>
                        <input type="text" id="promoCode" name="promoCode" placeholder="Введите промокод">
                    </div>
                    <div class="form-group">
                        <label for="deliveryDate">Дата и время доставки:</label>
                        <input type="datetime-local" id="deliveryDate" name="deliveryDate" required>
                    </div>
                    <div class="order-info">
                        <p>Сумма заказа: <span class="total-amount">${totalPrice} руб</span></p>
                        <p>Предполагаемая дата доставки: <span class="delivery-date"></span></p>
                    </div>
                    <button type="submit" class="submit-order">Подтвердить заказ</button>
                </form>
            </div>
        </div>
    </main>
</body>
</html>
