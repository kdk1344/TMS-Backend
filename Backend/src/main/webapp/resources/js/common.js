/**
 * TMS 서비스의 기본 URL을 추가하여 매번 기본 URL을 작성하지 않도록 하기 위한 fetch 함수입니다.
 *
 * @template T
 * @param {RequestInfo} url - 요청할 URL입니다.
 * @param {RequestInit} [options] - 선택적인 fetch 옵션입니다. 예를 들어, method, headers, body 등을 설정할 수 있습니다.
 * @returns {Promise<T>} - JSON 형식의 데이터를 반환하는 Promise입니다. 응답이 JSON 형식이 아니면 응답 객체를 반환합니다.
 * @throws {Error} - 응답 상태 코드가 오류(예: 4xx 또는 5xx)를 나타내거나 fetch 작업이 실패한 경우 `Error`를 던집니다.
 */
export async function tmsFetch(url, options) {
  const TMS_BASE_URL = "/tms/api";

  return fetchAPI(TMS_BASE_URL + url, options);
}

/**
 * API에서 데이터를 가져오고 다양한 HTTP 상태 오류를 처리합니다.
 *
 * @template T
 * @param {RequestInfo} url - 가져올 URL입니다. 문자열 또는 `Request` 객체일 수 있습니다.
 * @param {RequestInit} [options] - 선택적으로 fetch 요청에 대한 옵션을 설정할 수 있습니다. 예: method, headers, body 등.
 * @returns {Promise<T>} - JSON 형식의 데이터를 반환하는 Promise입니다. 응답이 JSON 형식이 아니면 응답 객체를 반환합니다.
 * @throws {Error} - 응답 상태 코드가 오류(예: 4xx 또는 5xx)를 나타내거나 fetch 작업이 실패한 경우 `Error`를 던집니다.
 *
 * @example
 * fetchAPI('/api/data', { method: 'GET' })
 *   .then(data => console.log(data))
 *   .catch(error => console.error(error));
 */
export async function fetchAPI(url, options) {
  try {
    const response = await fetch(url, options);

    if (!response.ok) {
      if (response.status >= 500) throw new Error("[Error 500] 서버에 문제가 생겼습니다.");
      if (response.status === 401) throw new Error("[Error 401] 아이디와 비밀번호를 확인해주세요.");
      if (response.status === 403) throw new Error("[Error 403] 접근 권한이 없습니다.");
      if (response.status === 404) throw new Error("[Error 404] 요청하신 페이지를 찾을 수 없습니다.");
      if (response.status === 409) throw new Error("[Error 409] 기존 리소스와 충돌이 발생했습니다.(ex. 중복 아이디)");
      if (response.status >= 400) throw new Error("[Error] 유효하지 않은 요청입니다.");
    }

    const contentType = response.headers.get("content-type");
    const isJsonType = contentType && contentType.includes("application/json");

    if (isJsonType) return response.json();

    return response;
  } catch (error) {
    console.error(error);
    throw error;
  }
}
