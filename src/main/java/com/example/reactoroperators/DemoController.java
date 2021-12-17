package com.example.reactoroperators;

import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.CorePublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class DemoController {

  @GetMapping("api/operators")
  public Publisher<?> operators() {
    return Mono.just(1) // Mono<Integer>
      .flatMap(integer -> convertirALetras(integer)) // Mono<IntegerToLettersWrapper>
      .map(x -> {
        Integer integer = x.getInteger();
        String letters = x.getLetters();
        log.info("integer: {}", integer);
        log.info("letters: {}", letters);
        return x.getLetters();
      }); // Mono<String>
  }

  Mono<IntegerToLettersWrapper> convertirALetras(Integer i) {
    return Mono.just(new IntegerToLettersWrapper("Uno", i));
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  static class IntegerToLettersWrapper {
    private String letters;
    private Integer integer;
  }

  Mono<String> libreriaDeMiEmpresa() {
    return Mono.just(getDataFromWS());
  }

  // Aplicacion migrando -> Callable
  Supplier<String> getData1() {
    return () -> "X";
  }

  String getDataFromWS() {
    log.info("getDataFromWS");
    return "Empleado activo"; // Llamo a un WS y trajo cierta data
  }



}
