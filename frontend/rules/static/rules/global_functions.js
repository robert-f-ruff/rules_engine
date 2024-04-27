function hideControl(element) {
  let element_class = element.getAttribute("class");
  element_class = element_class + " d-none";
  element.setAttribute("class", element_class);
}