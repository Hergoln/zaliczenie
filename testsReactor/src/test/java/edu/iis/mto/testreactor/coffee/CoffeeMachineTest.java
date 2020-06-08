package edu.iis.mto.testreactor.coffee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.iis.mto.testreactor.coffee.milkprovider.MilkProvider;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CoffeeMachineTest {

    @Mock
    private MilkProvider milkProvider;
    @Mock
    private Grinder grinder;
    @Mock
    private CoffeeReceipes receipes;
    private CoffeeMachine coffeeMachine;

    @BeforeEach
    void setUp() {
        coffeeMachine= new CoffeeMachine(grinder, milkProvider, receipes);
    }

    @Test
    public void properCoffeeOrderShouldReturnProperCoffee() {

        CoffeType type =  CoffeType.LATTE;
        CoffeeSize size = CoffeeSize.STANDARD;
        Double grindWieght = 1.0d;
        Integer waterAmount = 1;

        Map<CoffeeSize, Integer> waterAmounts = new HashMap<>();
        waterAmounts.put(size, waterAmount);
        CoffeeReceipe receipe = CoffeeReceipe.builder().withWaterAmounts(waterAmounts).build();

        when(receipes.getReceipe(type)).thenReturn(Optional.of(receipe));
        when(grinder.canGrindFor(size)).thenReturn(true);
        when(grinder.grind(size)).thenReturn(1.0d);

        CoffeOrder order = CoffeOrder.builder().withSize(size).withType(type).build();

        Coffee result = coffeeMachine.make(order);

        Coffee expected = new Coffee();
        expected.setCoffeeWeigthGr(grindWieght);
        expected.setWaterAmount(waterAmount);

        assertEquals(result.getCoffeeWeigthGr(), expected.getCoffeeWeigthGr());
        assertEquals(result.getWaterAmount(), expected.getWaterAmount());
        assertEquals(result.getMilkAmout(), expected.getMilkAmout());
    }

}
