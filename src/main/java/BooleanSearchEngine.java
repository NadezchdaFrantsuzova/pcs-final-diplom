import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private Map<String, List<PageEntry>> result = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) {

        for (File pdf : pdfsDir.listFiles()) {

            try (PdfDocument doc = new PdfDocument(new PdfReader(pdf))) {

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
                    if (result.containsKey(kv.getKey())) {
                        pageEntryList = result.get(kv.getKey());
                    } else {
                        pageEntryList = new ArrayList<>();
                    }
                    pageEntryList.add(new PageEntry(pdf.getName(), doc.getNumberOfPages(), kv.getValue()));
                    result.put(kv.getKey(), pageEntryList);
                    pageEntryList.sort(PageEntry::compareTo);
                }
            } catch (FileNotFoundException e) {
                System.out.println("Файл не найден");
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        return result.getOrDefault(word, Collections.EMPTY_LIST);
    }
}
