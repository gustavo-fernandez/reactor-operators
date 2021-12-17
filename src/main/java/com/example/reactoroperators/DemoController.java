package com.example.reactoroperators;

import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@Slf4j
public class DemoController {

  @GetMapping("api/operators")
  public Publisher<?> operators() {
    Mono<DataWS> llamarAWs01 = Mono.fromCallable(() -> getData("RESUL"));//.subscribeOn(Schedulers.parallel());
    Mono<DataWS> llamarAWs02 = Mono.fromCallable(() -> getData("TADO"));//.subscribeOn(Schedulers.boundedElastic());

    return Mono
      .zip(llamarAWs01, llamarAWs02, DemoController::concatTwoDatas)
      .subscribeOn(Schedulers.boundedElastic())
      .publishOn(Schedulers.newParallel("mi-hilito"))
      .map(DemoController::convertToFinalResult);
  }

  private static Data02 concatTwoDatas(DataWS resultadoMono01, DataWS resultadoMono02) {
    log.info("con el input inicial era: {}", resultadoMono01.getParameter() + resultadoMono02.getParameter());
    var concatenation = resultadoMono01.getResult() + resultadoMono02.getResult();
    return Data02.builder()
      .parameter01(resultadoMono01.getParameter())
      .parameter02(resultadoMono02.getParameter())
      .concatenationResult(concatenation)
      .build();
  }

  private static String convertToFinalResult(Data02 data) {
    log.info("convertToFinalResult");
    return data.getParameter01() + "*" + data.getParameter02() + "-" + data.getConcatenationResult();
  }

  private DataWS getData(String data) {
    sleep(5_000);
    log.info("getData({})", data);
    return new DataWS(data, data.toLowerCase());
  }

  @SneakyThrows
  private static void sleep(long time) {
    Thread.sleep(time);
  }

  @Builder
  @Getter
  static class DataWS {
    private String parameter;
    private String result;
  }

  @Builder
  @Getter
  static class Data02 {
    private String parameter01;
    private String parameter02;
    private String concatenationResult;
  }

}
