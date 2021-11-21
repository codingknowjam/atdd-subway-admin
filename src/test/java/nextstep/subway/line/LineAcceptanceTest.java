package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        // 파라미터 준비
        Map<String, String> params = new HashMap<>();
        params.put("name", "천안역");
        params.put("color", "blue");

        // when
        // 지하철_노선_생성_요청
        Response response = requestCreateLines(params);

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }



    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = new HashMap<>();
        params.put("name", "천안역");
        params.put("color", "blue");

        requestCreateLines(params);

        // when
        // 지하철_노선_중복생성_요청
        Response response = requestCreateLines(params);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        Map<String, String> params = new HashMap<>();
        params.put("name", "천안역");
        params.put("color", "blue");
        Map<String, String> otherParams = new HashMap<>();
        otherParams.put("name", "서울역");
        otherParams.put("color", "blue");

        Response createResponse = requestCreateLines(params);
        Response otherCreateResponse = requestCreateLines(otherParams);

        // when
        // 지하철_노선_목록_조회_요청
        Response response = requestFindAllLines();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        List<Long> findIds = response.jsonPath().getList(".", LineResponse.class)
            .stream()
            .map(lineResponse -> lineResponse.getId())
            .collect(Collectors.toList());

        List<Long> createIds = Arrays.asList(createResponse, otherCreateResponse).stream()
            .map(res -> Long.parseLong(res.getHeader("Location").split("/")[2]))
            .collect(Collectors.toList());

        assertThat(findIds).containsAll(createIds);
    }

    private Response requestFindAllLines() {
        return RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract().response();
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_조회_요청

        // then
        // 지하철_노선_응답됨
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_수정_요청

        // then
        // 지하철_노선_수정됨
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_제거_요청

        // then
        // 지하철_노선_삭제됨
    }

    private Response requestCreateLines(Map<String, String> params) {
        Response response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
            .when()
            .post("/lines")
            .then().log().all()
            .extract()
            .response();
        return response;
    }
}
