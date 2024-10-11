const $ = (selector) => {
    const $HTMLElement = document.querySelector(selector);
    if (!$HTMLElement) throw new Error(`Dom Error`);
  
    return $HTMLElement;
  };
  
const $$ = (selector) => {
    return document.querySelectorAll(selector);
};
  
export { $, $$ };