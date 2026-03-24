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
        
        // 2. Suspicious Characters count (@, -, _, etc) and Subdomains
        long suspChars = urlStr.chars().filter(ch -> ch == '@' || ch == '-' || ch == '_').count();
        features.setNumSuspiciousChars(suspChars);
        
        // Count subdomains (approximated by number of dots in the host)
        try {
            URI uri = new URI(urlStr);
            String host = uri.getHost();
            if (host != null) {
                long numDots = host.chars().filter(ch -> ch == '.').count();
                // E.g., www.example.com has 2 dots. 
                // We add dots > 2 to suspicious chars count to leverage existing schema.
                if (numDots > 2) {
                    features.setNumSuspiciousChars(suspChars + (numDots - 2));
                }
                
                // Check if host is an IP address
                if (host.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) {
                     features.setNumSuspiciousChars(features.getNumSuspiciousChars() + 5); 
                }
            }
        } catch (Exception e) {
            // Ignore URI parsing error
        }

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
                    .timeout(1000)
                    .get();

            URI baseUri = new URI(urlStr);
            String host = baseUri.getHost();
            if (host == null) host = "";
            String finalHost = host;

            // 4. Has Suspicious Iframes (hidden)
            Elements iframes = doc.select("iframe");
            boolean badIframe = iframes.stream().anyMatch(iframe -> {
                String style = iframe.attr("style").toLowerCase().replaceAll("\\s+", "");
                String width = iframe.attr("width");
                String height = iframe.attr("height");
                return style.contains("display:none") || 
                       style.contains("visibility:hidden") ||
                       style.contains("opacity:0") ||
                       style.contains("width:0") ||
                       style.contains("height:0") ||
                       style.contains("position:absolute;left:-") ||
                       "0".equals(width) || "0".equals(height) ||
                       "1".equals(width) || "1".equals(height);
            });
            features.setHasSuspiciousIframes(badIframe ? 1.0 : 0.0);

            // 5. External Link Ratio
            Elements links = doc.select("a[href]");
            int totalLinks = links.size();
            int externalLinks = 0;
            for (Element link : links) {
                String absHref = link.attr("abs:href");
                if (absHref.startsWith("http")) {
                    try {
                        URI linkUri = new URI(absHref);
                        String linkHost = linkUri.getHost();
                        if (isExternal(linkHost, finalHost)) {
                            externalLinks++;
                        }
                    } catch (Exception e) {
                        externalLinks++; // Unparseable, consider external
                    }
                }
            }
            double ratio = totalLinks == 0 ? 0 : (double) externalLinks / totalLinks;
            features.setExternalLinkRatio(ratio);

            // 6. Fake Login Form (password input submitting externally or via JavaScript)
            Elements passwordInputs = doc.select("input[type=password]");
            boolean hasFakeLogin = false;
            
            // Also check for common "text" inputs that are named suspiciously in hidden forms
            if (passwordInputs.isEmpty()) {
                passwordInputs = doc.select("input[name*=pass], input[name*=pwd], input[name*=credential]");
            }

            if (!passwordInputs.isEmpty()) {
                Elements forms = doc.select("form");
                for (Element form : forms) {
                    if (!form.select("input[type=password], input[name*=pass], input[name*=pwd]").isEmpty()) {
                        String action = form.attr("action").trim();
                        String absAction = form.attr("abs:action").trim();
                        
                        // Suspicious if empty, #, or javascript
                        if (action.isEmpty() || action.startsWith("#") || action.toLowerCase().startsWith("javascript:")) {
                            hasFakeLogin = true;
                            break;
                        }
                        
                        if (absAction.startsWith("http")) {
                            try {
                                URI actionUri = new URI(absAction);
                                String actionHost = actionUri.getHost();
                                if (isExternal(actionHost, finalHost)) {
                                    hasFakeLogin = true;
                                    break;
                                }
                            } catch (Exception e) {
                                hasFakeLogin = true;
                                break;
                            }
                        }
                    }
                }
            }
            features.setHasFakeLoginForm(hasFakeLogin ? 1.0 : 0.0);
            
        } catch (Exception e) {
            // If request fails (timeout, 404, etc.), default content features to high risk or neutral
            features.setHasSuspiciousIframes(0.0);
            features.setExternalLinkRatio(0.5); 
            features.setHasFakeLoginForm(0.0);
        }

        return features;
    }

    private boolean isExternal(String host1, String host2) {
        if (host1 == null || host2 == null || host1.isEmpty() || host2.isEmpty()) return true;
        if (host1.equals(host2)) return false;
        String h1 = host1.startsWith("www.") ? host1.substring(4) : host1;
        String h2 = host2.startsWith("www.") ? host2.substring(4) : host2;
        return !h1.equals(h2) && !h1.endsWith("." + h2) && !h2.endsWith("." + h1);
    }
}
