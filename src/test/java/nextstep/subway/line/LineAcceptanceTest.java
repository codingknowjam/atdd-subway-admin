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
	private static Map<String, String> params = new HashMap<>();
	static{
		params.put("name", "천안역");
		params.put("color", "blue");
	}

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// given
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
		Map<String, String> otherParams = new HashMap<>();
		otherParams.put("name", "서울역");
		otherParams.put("color", "blue");

		Response createResponse = requestCreateLines(params);
		Response otherCreateResponse = requestCreateLines(otherParams);

		// when
		// 지하철_노선_목록_조회_요청
		Response findResponse = requestFindAllLines();

		// then
		// 지하철_노선_목록_응답됨
		// 지하철_노선_목록_포함됨
		List<Long> findIds = findResponse.jsonPath().getList(".", LineResponse.class)
			.stream()
			.map(lineResponse -> lineResponse.getId())
			.collect(Collectors.toList());

		List<Long> createIds = Arrays.asList(createResponse, otherCreateResponse).stream()
			.map(res -> Long.parseLong(res.getHeader("Location").split("/")[2]))
			.collect(Collectors.toList());

		assertThat(findIds).containsAll(createIds);
	}

	@DisplayName("지하철 노선 조회 성공")
	@Test
	void getLineSuccess() {
		// given
		// 지하철_노선_등록되어_있음
		Response createResponse = requestCreateLines(params);
		String url = createResponse.header("Location");

		// when
		// 지하철_노선_조회_요청
		Response findResponse = requestFindLine(url);

		// then
		// 지하철_노선_응답됨
		Long findId = findResponse.jsonPath().getObject(".", LineResponse.class).getId();
		Long createId = Long.parseLong(url.split("/")[2]);
		assertThat(findId).isEqualTo(createId);
	}

	@DisplayName("지하철 노선 조회 실패")
	@Test
	void getLineFail() {
		// given
		// 지하철_노선_등록되어_있음
		Response createResponse = requestCreateLines(params);
		String url = createResponse.header("Location");

		// when
		// 지하철_노선_조회_요청
		Response findResponse = requestFindLine("/lines/3");

		// then
		// 지하철_노선_응답됨
		assertThat(findResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}


	@DisplayName("지하철 노선을 수정 성공")
	@Test
	void updateLineSuccess() {
		// given
		// 지하철_노선_등록되어_있음
		Map<String, String> updateParams = new HashMap<>();
		updateParams.put("name", "서울역");
		updateParams.put("color", "blue");
		Response createResponse = requestCreateLines(params);
		String url = createResponse.header("Location");

		// when
		// 지하철_노선_수정_요청
		Response updateResponse = requestUpdateLine(url, updateParams);

		// then
		// 지하철_노선_수정됨
		LineResponse lineResponse = updateResponse.jsonPath().getObject(".", LineResponse.class);
		assertThat(lineResponse.getName()).isEqualTo("서울역");
		assertThat(lineResponse.getId()).isEqualTo(Long.parseLong(url.split("/")[2]));
	}

	@DisplayName("지하철 노선을 수정 실패")
	@Test
	void updateLineFail() {
		// given
		// 지하철_노선_등록되어_있음
		Map<String, String> updateParams = new HashMap<>();
		updateParams.put("name", "서울역");
		updateParams.put("color", "blue");
		Response createResponse = requestCreateLines(params);
		String url = createResponse.header("Location");

		// when
		// 지하철_노선_수정_요청
		Response updateResponse = requestUpdateLine("lines/3", updateParams);

		// then
		// 지하철_노선_수정됨
		assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선을 제거 성공")
	@Test
	void deleteLineSuccess() {
		// given
		// 지하철_노선_등록되어_있음
		Map<String, String> otherParams = new HashMap<>();
		otherParams.put("name", "서울역");
		otherParams.put("color", "blue");
		Response createResponse = requestCreateLines(params);
		requestCreateLines(otherParams);
		String url = createResponse.header("Location");

		// when
		// 지하철_노선_제거_요청
		requestDeleteLine(url);
		List<LineResponse> findAllResponse = requestFindAllLines().jsonPath().getList(".", LineResponse.class);

		// then
		// 지하철_노선_삭제됨
		assertThat(findAllResponse).hasSize(1);
	}

	@DisplayName("지하철 노선을 제거 실패")
	@Test
	void deleteLineFail() {
		// given
		// 지하철_노선_등록되어_있음
		requestCreateLines(params);
		// when
		// 지하철_노선_제거_요청
		Response deleteResponse = requestDeleteLine("/lines/3");

		// then
		// 지하철_노선_삭제됨
		assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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

	private Response requestFindAllLines() {
		return RestAssured.given().log().all()
			.when()
			.get("/lines")
			.then().log().all()
			.extract().response();
	}

	private Response requestFindLine(String url) {
		return RestAssured.given().log().all()
			.when()
			.get(url)
			.then().log().all()
			.extract().response();
	}

	private Response requestUpdateLine(String url, Map<String, String> updateParams) {
		return RestAssured.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(updateParams)
			.when()
			.patch(url)
			.then().log().all()
			.extract().response();
	}

	private Response requestDeleteLine(String url) {
		return RestAssured.given().log().all()
			.when()
			.delete(url)
			.then().log().all()
			.extract().response();
	}

}
