package edu.iis.mto.testreactor.coffee;

import edu.iis.mto.testreactor.coffee.milkprovider.MilkProvider;
import edu.iis.mto.testreactor.coffee.milkprovider.MilkProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.inOrder;
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
    private CoffeType type;
    private CoffeeSize size;
    private Double grindWieght;
    private Integer waterAmount;
    private Integer milkAmount;

    @BeforeEach
    void setUp() {
        coffeeMachine = new CoffeeMachine(grinder, milkProvider, receipes);
        type = CoffeType.LATTE;
        size = CoffeeSize.STANDARD;
        grindWieght = 1.0d;
        waterAmount = 1;
        milkAmount = 1;
    }

    @Test
    public void properCoffeeOrderShouldReturnProperCoffee() {
        CoffeType type = CoffeType.LATTE;
        CoffeeSize size = CoffeeSize.STANDARD;
        Double grindWieght = 1.0d;
        Integer waterAmount = 1;

        Map<CoffeeSize, Integer> waterAmounts = new HashMap<>();
        waterAmounts.put(size, waterAmount);
        CoffeeReceipe receipe = CoffeeReceipe.builder().withWaterAmounts(waterAmounts).build();

        when(receipes.getReceipe(type)).thenReturn(Optional.of(receipe));
        when(grinder.canGrindFor(size)).thenReturn(true);
        when(grinder.grind(size)).thenReturn(grindWieght);

        CoffeOrder order = CoffeOrder.builder().withSize(size).withType(type).build();
        Coffee result = coffeeMachine.make(order);
        Coffee expected = expectedCoffeeOf(grindWieght, waterAmount, noMilk);

        assertEquals(result.getCoffeeWeigthGr(), expected.getCoffeeWeigthGr());
        assertEquals(result.getWaterAmount(), expected.getWaterAmount());
        assertEquals(result.getMilkAmout(), expected.getMilkAmout());
    }

    @Test
    void coffeeReceipeWithMilkShouldCallMilkProviderMethods() throws MilkProviderException {
        Map<CoffeeSize, Integer> waterAmounts = new HashMap<>();
        waterAmounts.put(size, waterAmount);
        CoffeeReceipe receipe = CoffeeReceipe.builder().withWaterAmounts(waterAmounts).withMilkAmount(milkAmount).build();

        when(receipes.getReceipe(type)).thenReturn(Optional.of(receipe));
        when(grinder.canGrindFor(size)).thenReturn(true);
        when(grinder.grind(size)).thenReturn(grindWieght);

        CoffeOrder order = CoffeOrder.builder().withSize(size).withType(type).build();
        coffeeMachine.make(order);

        InOrder callOrder = inOrder(milkProvider);

        callOrder.verify(milkProvider).heat();
        callOrder.verify(milkProvider).pour(milkAmount);
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
