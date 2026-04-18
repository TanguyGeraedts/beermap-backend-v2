package be.tanguy.beermap.api.stats.service.calculator;

import be.tanguy.beermap.api.logs.repository.DrinkLogRepository;

import java.lang.reflect.Proxy;
import java.util.Map;

final class StatsCalculatorTestSupport {

    private StatsCalculatorTestSupport() {
    }

    static DrinkLogRepository repositoryReturning(Map<String, Object> resultsByMethodName) {
        return (DrinkLogRepository) Proxy.newProxyInstance(
                DrinkLogRepository.class.getClassLoader(),
                new Class<?>[]{DrinkLogRepository.class},
                (proxy, method, args) -> {
                    if (resultsByMethodName.containsKey(method.getName())) {
                        return resultsByMethodName.get(method.getName());
                    }

                    throw new UnsupportedOperationException("Unexpected repository method: " + method.getName());
                }
        );
    }
}
