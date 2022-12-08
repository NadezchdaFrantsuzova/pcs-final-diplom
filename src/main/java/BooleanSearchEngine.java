import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    Map<String, List<PageEntry>> result = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) {

        for (File pdf : pdfsDir.listFiles()) {

            String pdfName = pdf.getName();

            try (PdfDocument doc = new PdfDocument(new PdfReader(pdf))) {

                int page = doc.getNumberOfPages();
                Map<String, Integer> freqs = new HashMap<>();
                List<PageEntry> pageEntryList;

                for (int i = 1; i <= doc.getNumberOfPages(); i++) {

                    var text = PdfTextExtractor.getTextFromPage(doc.getPage(i));
                    var words = text.split("\\P{IsAlphabetic}+");

                    for (var word : words) {
                        if (word.isEmpty()) {
                            continue;
                        }
                        word = word.toLowerCase();
                        freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                    }
                }
                for (var kv : freqs.entrySet()) {
                    String wordResult = kv.getKey();
                    int count = kv.getValue();

                    if (result.containsKey(wordResult)) {
                        pageEntryList = result.get(wordResult);
                    } else {
                        pageEntryList = new ArrayList<>();
                    }
                    pageEntryList.add(new PageEntry(pdfName, page, count));

                    Collections.sort(pageEntryList, Collections.reverseOrder());
                    result.put(wordResult, pageEntryList);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        return result.get(word);
    }
}
