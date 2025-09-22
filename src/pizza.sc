require: pizza.csv
    name = pizza
    var = pizza

theme: /

    state: ChoosePizza
        a: Какую пиццу будем заказывать сегодня?
        script:
            for (var id = 1; id < Object.keys(pizza).length + 1; id++) {
                var regions = pizza[id].value.region;
                if (_.contains(regions, $client.city)) {
                    var button_name = pizza[id].value.title;
                    $reactions.buttons({text: button_name, transition: 'GetName'})
                }
            }

        state: GetName
            script:
                $session.pizza_name = $request.query;
            go!: /ChooseVariant

        state: ClickButtons
            q: *
            a: Нажмите, пожалуйста, кнопку.
            go!: ..

    state: ChooseVariant
        a: Выберите, пожалуйста, вариант:
        script:
            for (var id = 1; id < Object.keys(pizza).length + 1; id++) {
                if ($session.pizza_name == pizza[id].value.title) {
                    var variations = pizza[id].value.variations;
                    for(var i = 0; i < variations.length; i++){
                        var button_name = variations[i].name + " за " + variations[i].price + " руб."
                        $reactions.inlineButtons({text: button_name, callback_data: variations[i].id })
                    }
                }
            }
        buttons:
            "Меню" -> /ChoosePizza

        state: ClickButtons
                q: *
                a: Нажмите, пожалуйста, кнопку.
                go!: ..

    state: GetVariant
        event: telegramCallbackQuery
        script:
            $session.pizza_id = parseInt($request.query);
        go!: /ChooseQuantity

    state: ChooseQuantity
        a: Выберите, пожалуйста, количество:
        buttons:
            "1" -> ./GetQuantity
            "2" -> ./GetQuantity
            "3" -> ./GetQuantity

        state: ClickButtons
                q: *
                a: Нажмите, пожалуйста, кнопку.
                go!: ..

        state: GetQuantity
            script:
                $session.quantity = parseInt($request.query);
                $session.cart.push({name: $session.pizza_name, id: $session.pizza_id, quantity: $session.quantity});
            a: Хотите ли выбрать что-нибудь еще, или перейдем к оформлению заказа?
            buttons:
                "Меню" -> /ChoosePizza
            buttons:
                "Оформить заказ" -> /Cart

            state: ClickButtons
                q: *
                a: Нажмите, пожалуйста, кнопку.
                go!: ..