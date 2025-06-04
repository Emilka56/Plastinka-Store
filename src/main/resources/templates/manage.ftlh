<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Управление скидками</title>
    <link rel="stylesheet" href="/styles/global.css">
    <link rel="stylesheet" href="/styles/font.css">
    <style>
        body {
            background-color: #fff;
            color: #666;
            font-family: Arial, sans-serif;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }

        .discount-form {
            max-width: 500px;
            margin: 20px auto;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
            background-color: #fff;
        }

        .discounts-list {
            max-width: 800px;
            margin: 20px auto;
            background-color: #fff;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .btn {
            padding: 8px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        .btn-primary {
            background-color: #851414;
            color: white;
        }

        .btn-primary:hover {
            background-color: #6a1010;
        }

        .btn-danger {
            background-color: #d9534f;
            color: white;
        }

        .btn-danger:hover {
            background-color: #c9302c;
        }

        .btn-edit {
            background-color: #5bc0de;
            color: white;
            margin-right: 5px;
        }

        .btn-edit:hover {
            background-color: #46b8da;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }

        th {
            background-color: #851414;
            color: white;
        }

        tr:hover {
            background-color: #f5f5f5;
        }

        .form-group {
            margin-bottom: 15px;
        }

        .form-control {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }

        .alert {
            padding: 10px;
            margin-top: 10px;
            border-radius: 4px;
            display: none;
        }

        .alert-success {
            background-color: #dff0d8;
            color: #3c763d;
        }

        .alert-danger {
            background-color: #f2dede;
            color: #a94442;
        }

        .modal {
            display: none;
            position: fixed;
            z-index: 1;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.4);
        }

        .modal-content {
            background-color: #fff;
            margin: 15% auto;
            padding: 20px;
            border: 1px solid #888;
            width: 50%;
            border-radius: 5px;
        }

        .close {
            color: #aaa;
            float: right;
            font-size: 28px;
            font-weight: bold;
            cursor: pointer;
        }

        .close:hover {
            color: #851414;
        }
    </style>
</head>
<body>
<#include "header.ftlh">
<input type="hidden" id="csrfToken" name="${_csrf.parameterName}" value="${_csrf.token}">

<div class="container">
    <h2 class="text-center mt-4">Управление скидками</h2>

    <!-- Форма создания скидки -->
    <div class="discount-form">
        <h4>Создать новую скидку</h4>
        <form id="discountForm">
            <div class="form-group">
                <label for="name">Название скидки</label>
                <input type="text" class="form-control" id="name" name="name" required>
            </div>
            <div class="form-group">
                <label for="percentageOff">Процент скидки</label>
                <input type="number" class="form-control" id="percentageOff" name="percentageOff" min="0" max="100" step="0.01" required>
            </div>
            <div class="form-group">
                <label for="promoCode">Промокод</label>
                <input type="text" class="form-control" id="promoCode" name="promoCode">
            </div>
            <button type="submit" class="btn btn-primary">Создать скидку</button>
        </form>
        <div class="alert alert-success" id="successAlert">Скидка успешно создана!</div>
        <div class="alert alert-danger" id="errorAlert"></div>
    </div>

    <!-- Список существующих скидок -->
    <div class="discounts-list">
        <h4>Существующие скидки</h4>
        <table class="table">
            <thead>
            <tr>
                <th>Название</th>
                <th>Процент скидки</th>
                <th>Промокод</th>
            </tr>
            </thead>
            <tbody>
            <#if discounts?? && discounts?has_content>
                <#list discounts as discount>
                    <tr>
                        <td>${discount.name}</td>
                        <td>${discount.percentageOff}%</td>
                        <td>${discount.promoCode!'—'}</td>
                        <td>
                            <button class="btn btn-edit edit-discount" data-id="${discount.id}">Изменить</button>
                            <button class="btn btn-danger delete-discount" data-id="${discount.id}">Удалить</button>
                        </td>
                    </tr>
                </#list>
            <#else>
                <tr>
                    <td colspan="5" class="text-center">Нет активных скидок</td>
                </tr>
            </#if>
            </tbody>
        </table>
    </div>
</div>

<!-- Модальное окно для редактирования -->
<div id="editModal" class="modal">
    <div class="modal-content">
        <span class="close">&times;</span>
        <h3>Редактировать скидку</h3>
        <form id="editDiscountForm">
            <input type="hidden" id="editId" name="id">
            <div class="form-group">
                <label for="editName">Название скидки</label>
                <input type="text" class="form-control" id="editName" name="name" required>
            </div>
            <div class="form-group">
                <label for="editPercentageOff">Процент скидки</label>
                <input type="number" class="form-control" id="editPercentageOff" name="percentageOff" min="0" max="100" step="0.01" required>
            </div>
            <div class="form-group">
                <label for="editPromoCode">Промокод</label>
                <input type="text" class="form-control" id="editPromoCode" name="promoCode">
            </div>
            <button type="submit" class="btn btn-primary">Сохранить изменения</button>
        </form>
        <div class="alert alert-success" id="editSuccessAlert" style="display: none;"></div>
        <div class="alert alert-danger" id="editErrorAlert" style="display: none;"></div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
<script>
    const csrfToken = document.getElementById("csrfToken")?.value;
    $(document).ready(function() {
        $.ajaxSetup({
            headers: {
                'X-CSRF-TOKEN': csrfToken
            }
        });
        // Создание скидки
        $('#discountForm').on('submit', function(e) {
            e.preventDefault();
            $('#errorAlert').hide();

            $.ajax({
                url: '/discounts/create',
                method: 'POST',
                data: {
                    name: $('#name').val(),
                    percentageOff: $('#percentageOff').val(),
                    promoCode: $('#promoCode').val() || null
                },
                success: function(response) {
                    if (response.status === 'success') {
                        $('#successAlert').show();
                        setTimeout(function() {
                            location.reload();
                        }, 1000);
                    }
                },
                error: function(xhr) {
                    $('#errorAlert').text(xhr.responseText).show();
                }
            });
        });

        // Удаление скидки
        $('.delete-discount').on('click', function() {
            const id = $(this).data('id');
            if (confirm('Вы уверены, что хотите удалить эту скидку?')) {
                $.ajax({
                    url: '/discounts/' + id,
                    method: 'DELETE',
                    success: function() {
                        location.reload();
                    },
                    error: function(xhr) {
                        alert('Ошибка при удалении скидки: ' + xhr.responseText);
                    }
                });
            }
        });

        // Редактирование скидки
        const modal = $('#editModal');
        const span = $('.close');

        $('.edit-discount').on('click', function() {
            const id = $(this).data('id');

            $.ajax({
                url: '/discounts/' + id,
                method: 'GET',
                success: function(discount) {
                    $('#editId').val(discount.id);
                    $('#editName').val(discount.name);
                    $('#editPercentageOff').val(discount.percentageOff);
                    $('#editPromoCode').val(discount.promoCode || '');
                    modal.show();
                },
                error: function(xhr) {
                    alert('Ошибка при загрузке данных скидки: ' + xhr.responseText);
                }
            });
        });

        span.on('click', function() {
            modal.hide();
        });

        $(window).on('click', function(event) {
            if (event.target == modal[0]) {
                modal.hide();
            }
        });

        $('#editDiscountForm').on('submit', function(e) {
            e.preventDefault();
            $('#editErrorAlert').hide();

            const id = $('#editId').val();
            const data = {
                name: $('#editName').val(),
                percentageOff: $('#editPercentageOff').val(),
                promoCode: $('#editPromoCode').val() || null,
            };

            $.ajax({
                url: '/discounts/' + id,
                method: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify(data),
                success: function(response) {
                    $('#editSuccessAlert').text('Скидка успешно обновлена!').show();
                    setTimeout(function() {
                        location.reload();
                    }, 1000);
                },
                error: function(xhr) {
                    $('#editErrorAlert').text(xhr.responseText).show();
                }
            });
        });
    });
</script>
</body>
</html>