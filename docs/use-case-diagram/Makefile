SRC    := use-case-diagram.mmd
NAME   := use-case-diagram
MMDC   := mmdc

# Output files
SVG_WHITE := $(NAME).svg
SVG_TRANS := $(NAME)-transparent.svg
PNG_WHITE := $(NAME).png
PNG_TRANS := $(NAME)-transparent.png

.PHONY: all svg png clean

all: svg png

svg: $(SVG_WHITE) $(SVG_TRANS)

png: $(PNG_WHITE) $(PNG_TRANS)

$(SVG_WHITE): $(SRC)
	$(MMDC) -i $< -o $@ -b white

$(SVG_TRANS): $(SRC)
	$(MMDC) -i $< -o $@ -b transparent

$(PNG_WHITE): $(SRC)
	$(MMDC) -i $< -o $@ -b white

$(PNG_TRANS): $(SRC)
	$(MMDC) -i $< -o $@ -b transparent

clean:
	rm -f $(SVG_WHITE) $(SVG_TRANS) $(PNG_WHITE) $(PNG_TRANS)
