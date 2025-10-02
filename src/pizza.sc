require: pizzas.js
theme: /
    state: ChoosePizza
        a: Какую пиццу будем заказывать сегодня?
        scriptEs6:
            _.forEach(pizzas, pizza => {
                if (_.contains(pizza.regions, $client.city)) {
                    $reactions.buttons({text: pizza.name, transition: 'GetName'})
                }
            })

        state: GetName
            script:
                $session.pizzaName = $request.query;
            go!: /ChooseVariant

        state: ClickButtons
            q: *
            a: Нажмите, пожалуйста, кнопку.
            go!: ..

    state: ChooseVariant
        a: Выберите, пожалуйста, вариант:
        scriptEs6:
            var pizzaVariations = _.find(pizzas, pz => {
                return pz.name === $session.pizzaName;
            }).variations
            _.forEach(pizzaVariations, variation => {
                var variationString = variation.name + " за " + variation.price + " руб."
                $reactions.buttons({text: variationString, transition: 'GetVariant' })
            })
            
        buttons:
            "Меню" -> /ChoosePizza

        state: ClickButtons
                q: *
                a: Нажмите, пожалуйста, кнопку.
                go!: ..

        state: GetVariant
            script:
                $session.variationString = $request.query
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
                $session.cart.push({name: $session.pizzaName, variationString: $session.variationString, quantity: $session.quantity});
            a: Хотите ли выбрать что-нибудь еще, или перейдем к оформлению заказа?
            buttons:
                "Меню" -> /ChoosePizza
            buttons:
                "Оформить заказ" -> /Cart

            state: ClickButtons
                q: *
                a: Нажмите, пожалуйста, кнопку.
                go!: ..