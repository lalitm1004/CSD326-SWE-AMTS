SRC    := use-case-diagram.mmd
NAME   := use-case-diagram
MMDC   := mmdc
OUTDIR := output

# Output files
SVG_WHITE := $(OUTDIR)/$(NAME).svg
SVG_TRANS := $(OUTDIR)/$(NAME)-transparent.svg
PNG_WHITE := $(OUTDIR)/$(NAME).png
PNG_TRANS := $(OUTDIR)/$(NAME)-transparent.png

.PHONY: all svg png clean

all: svg png

svg: $(SVG_WHITE) $(SVG_TRANS)

png: $(PNG_WHITE) $(PNG_TRANS)

$(OUTDIR):
	mkdir -p $(OUTDIR)

$(SVG_WHITE): $(SRC) | $(OUTDIR)
	$(MMDC) -i $< -o $@ -b white

$(SVG_TRANS): $(SRC) | $(OUTDIR)
	$(MMDC) -i $< -o $@ -b transparent

$(PNG_WHITE): $(SRC) | $(OUTDIR)
	$(MMDC) -i $< -o $@ -b white

$(PNG_TRANS): $(SRC) | $(OUTDIR)
	$(MMDC) -i $< -o $@ -b transparent

clean:
	rm -rf $(OUTDIR)
