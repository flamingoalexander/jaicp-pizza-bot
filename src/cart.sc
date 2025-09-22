require: function.js

theme: /

    state: Cart
        intent!: /корзина
        a: Ваша корзина:
        script:
            $temp.totalSum = 0;
            var n = 0;
            for(var i = 0; i < $session.cart.length; i++){
                var current_position = $session.cart[i];
                for(var id = 1; id < Object.keys(pizza).length + 1; id++){
                    if (current_position.name == pizza[id].value.title){
                        var variation = _.find(pizza[id].value.variations, function(variation){
                            return variation.id === current_position.id;
                        });

                        n++;

                        if (!variation) {
                            $reactions.answer("Что-то пошло не так, вариант не найден для id " + current_position.id);
                        } else {
                            $reactions.answer(n + ". " + current_position.name + ", " + variation.name + "\nЦена: " + variation.price + "\nКоличество: " + current_position.quantity);

                            $reactions.inlineButtons({text: "Удалить", callback_data: current_position.name});

                            $temp.totalSum += variation.price * current_position.quantity;
                        }
                    }
                }
            }
            $session.messageId = $request.rawRequest.message.message_id + n + 2;

        a: Общая сумма заказа: {{ $temp.totalSum }} рублей.
        a: Если все верно, отправьте свой номер телефона, и наш менеджер с вами свяжется.
        buttons:
            {text: "Отправить номер телефона", request_contact: true}
            "Меню" -> /ChoosePizza

        state: Edit
            event: telegramCallbackQuery
            script:
                var name = $request.rawRequest.callback_query.data;
                deleteFromCart(name);
                var message_id = $request.rawRequest.callback_query.message.message_id;

                editText(message_id, 'Удален');
                editText($session.messageId, 'Общая сумма заказа: ' + getTotalSum() + ' руб.');
            if: $session.cart.length == 0
                a: Вы очистили корзину
                go!: /ChoosePizza

            state: ClickButtons
                q: *
                a: Нажмите, пожалуйста, кнопку.
                go!: ..

    state: GetPhoneNumber
        event: telegramSendContact
        script:
            $client.phone_number = $request.rawRequest.message.contact.phone_number;
        a: Спасибо! Наш менеджер свяжется с вами по номеру телефона {{ $client.phone_number }}.