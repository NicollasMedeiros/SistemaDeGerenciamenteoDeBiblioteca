@InitBinder
public void initBinder(WebDataBinder binder) {
    binder.registerCustomEditor(Autor.class, new PropertyEditorSupport() {
        @Override
        public void setAsText(String text) {
            if (text == null || text.isBlank()) {
                setValue(null);
                return;
            }
            Long id = Long.valueOf(text);
            setValue(autorService.buscarPorId(id));
        }
    });
}