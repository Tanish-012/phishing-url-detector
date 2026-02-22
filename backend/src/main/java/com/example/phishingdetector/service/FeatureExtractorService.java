package com.example.phishingdetector.service;

import com.example.phishingdetector.domain.UrlFeatures;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Service
public class FeatureExtractorService {

    private static final List<String> PHISHING_KEYWORDS = Arrays.asList(
            "login", "signin", "secure", "account", "update", "verify", "bank", "confirm"
    );

    public UrlFeatures extractFeatures(String urlStr) {
        UrlFeatures features = new UrlFeatures();
        features.setUrl(urlStr);
        
        // 1. URL Length
        features.setUrlLength(urlStr.length());
        
        // 2. Suspicious Characters count (@, -, multiple subdomains)
        long suspChars = urlStr.chars().filter(ch -> ch == '@' || ch == '-').count();
        features.setNumSuspiciousChars(suspChars);
        
        // 3. Lexical Keywords
        long keywordCount = PHISHING_KEYWORDS.stream()
                .filter(keyword -> urlStr.toLowerCase().contains(keyword))
                .count();
        features.setNumLexicalKeywords(keywordCount);

        // Content Features Extraction
        try {
            // Attempt to fetch content with a brief timeout
            Document doc = Jsoup.connect(urlStr)
                    .userAgent("Mozilla/5.0")
                    .timeout(3000)
                    .get();

            URI baseUri = new URI(urlStr);
            String host = baseUri.getHost();

            // 4. Has Suspicious Iframes (hidden)
            Elements iframes = doc.select("iframe");
            boolean badIframe = iframes.stream().anyMatch(iframe -> 
                iframe.attr("src").contains("http") || 
                iframe.attr("style").contains("display:none") ||
                iframe.attr("width").equals("0")
            );
            features.setHasSuspiciousIframes(badIframe ? 1.0 : 0.0);

            // 5. External Link Ratio
            Elements links = doc.select("a[href]");
            int totalLinks = links.size();
            int externalLinks = 0;
            for (Element link : links) {
                String href = link.attr("href");
                if (href.startsWith("http") && host != null && !href.contains(host)) {
                    externalLinks++;
                }
            }
            double ratio = totalLinks == 0 ? 0 : (double) externalLinks / totalLinks;
            features.setExternalLinkRatio(ratio);

            // 6. Fake Login Form (password input submitting externally)
            Elements passwordInputs = doc.select("input[type=password]");
            boolean hasFakeLogin = false;
            if (!passwordInputs.isEmpty()) {
                Elements forms = doc.select("form");
                for (Element form : forms) {
                    if (!form.select("input[type=password]").isEmpty()) {
                        String action = form.attr("action");
                        if (action.startsWith("http") && host != null && !action.contains(host)) {
                            hasFakeLogin = true;
                            break;
                        }
                    }
                }
            }
            features.setHasFakeLoginForm(hasFakeLogin ? 1.0 : 0.0);
            
        } catch (Exception e) {
            // If request fails (timeout, 404, etc.), default content features to high risk or neutral
            // Phishing sites often go down or block scrapers.
            features.setHasSuspiciousIframes(0.0);
            features.setExternalLinkRatio(0.5); // moderate risk fallback
            features.setHasFakeLoginForm(0.0);
        }

        return features;
    }
}
