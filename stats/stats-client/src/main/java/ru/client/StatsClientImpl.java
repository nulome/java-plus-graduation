package ru.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.dto.StatCountHitsResponseDto;
import ru.dto.StatsSaveRequestDto;

import java.util.Arrays;
import java.util.List;

import static ru.related.Constants.CONTROLLER_HIT_PATH;
import static ru.related.Constants.CONTROLLER_STATS_PATH;


@Service
public class StatsClientImpl implements StatsClient {

    @Lookup
    public RestClient restClient() {
        return null;
    }

    @Override
    public List<StatCountHitsResponseDto> getStats(final String startTime, final String endTime, final List<String> uris, final Boolean unique) {
        RestClient restClient = restClient();
        Object[] listObj = restClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(CONTROLLER_STATS_PATH)
                        .queryParam("start", startTime)
                        .queryParam("end", endTime)
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .body(Object[].class);

        ObjectMapper mapper = new ObjectMapper();
        return Arrays.stream(listObj)
                .map(object -> mapper.convertValue(object, StatCountHitsResponseDto.class))
                .toList();
    }

    @Override
    public ResponseEntity<Void> saveRestClientNewStat(final StatsSaveRequestDto request) {
        return restClient().post()
                .uri(CONTROLLER_HIT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

}
