require: function.js

theme: /

    state: Cart
        intent!: /корзина
        a: Ваша корзина:
        script:
            # $temp.totalSum = 0;
            var counter = 1
            _.forEach($session.cart, function(cartItem) {
                $reactions.answer(counter + ". " + cartItem.name + ", " + cartItem.variationString + " в количестве " + cartItem.quantity + " шт ");
                counter++;
                # $temp.totalSum += variation.price * current_position.quantity;
            })
            # $session.messageId = $request.rawRequest.message.message_id + n + 2;

        # a: Общая сумма заказа: {{ $temp.totalSum }} рублей.
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