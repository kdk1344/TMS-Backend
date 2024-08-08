/**
 * TMS서비스의 기본 URL을 추가하여 매번 기본 url을 작성하지 않도록 하기 위한 fetch 함수
 * @template T
 * @param {RequestInfo} url - The URL to fetch.
 * @param {RequestInit} [options] - Optional fetch options.
 * @returns {Promise<T|null>} The fetched data in JSON format, or null if not JSON.
 * @throws {Error} Throws a Error if the response status is an error or if fetching fails.
 */
export const tmsFetch = async (url, options) => {
  const TMS_BASE_URL = "/tms/api";

  return fetchAPI(TMS_BASE_URL + url, options);
};

/**
 * Fetches data from an API and handles different HTTP status errors.
 * @template T
 * @param {RequestInfo} url - The URL to fetch.
 * @param {RequestInit} [options] - Optional fetch options.
 * @returns {Promise<T|null>} The fetched data in JSON format, or null if not JSON.
 * @throws {Error} Throws a Error if the response status is an error or if fetching fails.
 */
export const fetchAPI = async (url, options) => {
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

    return null;
  } catch (error) {
    console.error(error);
  }
};
