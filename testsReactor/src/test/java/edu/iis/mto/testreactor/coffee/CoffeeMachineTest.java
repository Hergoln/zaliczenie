package edu.iis.mto.testreactor.coffee;

import edu.iis.mto.testreactor.coffee.milkprovider.MilkProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoffeeMachineTest {

    @Mock
    private MilkProvider milkProvider;
    @Mock
    private Grinder grinder;
    @Mock
    private CoffeeReceipes receipes;
    private CoffeeMachine coffeeMachine;

    private Integer noMilk = null;

    @BeforeEach
    void setUp() {
        coffeeMachine = new CoffeeMachine(grinder, milkProvider, receipes);
    }

    @Test
    public void properCoffeeOrderShouldReturnProperCoffee() {
        CoffeType type = CoffeType.LATTE;
        CoffeeSize size = CoffeeSize.STANDARD;
        Double grindWieght = 1.0d;
        Integer waterAmount = 1;

        Map<CoffeeSize, Integer> waterAmounts = new HashMap<>();
        waterAmounts.put(size, waterAmount);
        CoffeeReceipe receipe = receipe(waterAmounts);

        when(receipes.getReceipe(type)).thenReturn(Optional.of(receipe));
        when(grinder.canGrindFor(size)).thenReturn(true);
        when(grinder.grind(size)).thenReturn(1.0d);

        CoffeOrder order = CoffeOrder.builder().withSize(size).withType(type).build();
        Coffee result = coffeeMachine.make(order);
        Coffee expected = expectedCoffeeOf(grindWieght, waterAmount, noMilk);

        assertEquals(result.getCoffeeWeigthGr(), expected.getCoffeeWeigthGr());
        assertEquals(result.getWaterAmount(), expected.getWaterAmount());
        assertEquals(result.getMilkAmout(), expected.getMilkAmout());
    }

    private CoffeeReceipe receipe(Map<CoffeeSize, Integer> waterAmounts) {
        return CoffeeReceipe.builder().withWaterAmounts(waterAmounts).build();
    }

    private Coffee expectedCoffeeOf(Double grindWeigh, Integer waterAmount, Integer milkAmount) {
        Coffee toReturn = new Coffee();
        toReturn.setWaterAmount(waterAmount);
        toReturn.setCoffeeWeigthGr(grindWeigh);
        if (milkAmount != null) {
            toReturn.setMilkAmout(milkAmount);
        }
        return toReturn;
    }


}
