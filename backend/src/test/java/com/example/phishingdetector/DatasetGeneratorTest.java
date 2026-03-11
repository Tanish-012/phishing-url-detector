package com.example.phishingdetector;

import com.example.phishingdetector.domain.UrlFeatures;
import com.example.phishingdetector.service.FeatureExtractorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@SpringBootTest
public class DatasetGeneratorTest {

    @Autowired
    private FeatureExtractorService featureExtractor;

    @Test
    public void generateDataset() throws IOException {
        List<String> benignUrls = Arrays.asList(
            "https://google.com", "https://youtube.com", "https://facebook.com", "https://twitter.com",
            "https://instagram.com", "https://linkedin.com", "https://apple.com", "https://microsoft.com",
            "https://wikipedia.org", "https://amazon.com", "https://netflix.com", "https://yahoo.com",
            "https://bing.com", "https://reddit.com", "https://twitch.tv", "https://pinterest.com",
            "https://github.com", "https://stackoverflow.com", "https://bbc.co.uk", "https://cnn.com",
            "https://nytimes.com", "https://reuters.com", "https://weather.com", "https://imdb.com",
            "https://ebay.com", "https://walmart.com", "https://target.com", "https://chase.com",
            "https://bankofamerica.com", "https://wellsfargo.com", "https://paypal.com", "https://adobe.com",
            "https://wordpress.org", "https://office.com", "https://blogspot.com", "https://aliexpress.com",
            "https://zillow.com", "https://hulu.com", "https://spotify.com", "https://dropbox.com",
            "https://www.google.com", "https://www.youtube.com", "https://www.facebook.com", "https://www.twitter.com",
            "https://www.instagram.com", "https://www.linkedin.com", "https://www.apple.com", "https://www.microsoft.com",
            "https://www.wikipedia.org", "https://www.amazon.com", "https://www.netflix.com", "https://www.yahoo.com",
            "https://www.bing.com", "https://www.reddit.com", "https://www.twitch.tv", "https://www.pinterest.com",
            "https://www.github.com", "https://www.stackoverflow.com", "https://www.bbc.co.uk", "https://www.cnn.com",
            "https://www.nytimes.com", "https://www.reuters.com", "https://www.weather.com", "https://www.imdb.com",
            "https://www.ebay.com", "https://www.walmart.com", "https://www.target.com", "https://www.chase.com",
            "https://www.bankofamerica.com", "https://www.wellsfargo.com", "https://www.paypal.com", "https://www.adobe.com",
            "https://www.wordpress.org", "https://www.office.com", "https://www.blogspot.com", "https://www.aliexpress.com",
            "https://www.zillow.com", "https://www.hulu.com", "https://www.spotify.com", "https://www.dropbox.com",
            "https://mail.google.com", "https://mail.youtube.com", "https://mail.facebook.com", "https://mail.twitter.com",
            "https://mail.instagram.com", "https://mail.linkedin.com", "https://mail.apple.com", "https://mail.microsoft.com",
            "https://mail.wikipedia.org", "https://mail.amazon.com", "https://mail.netflix.com", "https://mail.yahoo.com",
            "https://mail.bing.com", "https://mail.reddit.com", "https://mail.twitch.tv", "https://mail.pinterest.com",
            "https://mail.github.com", "https://mail.stackoverflow.com", "https://mail.bbc.co.uk", "https://mail.cnn.com",
            "https://mail.nytimes.com", "https://mail.reuters.com", "https://mail.weather.com", "https://mail.imdb.com",
            "https://mail.ebay.com", "https://mail.walmart.com", "https://mail.target.com", "https://mail.chase.com",
            "https://mail.bankofamerica.com", "https://mail.wellsfargo.com", "https://mail.paypal.com", "https://mail.adobe.com",
            "https://mail.wordpress.org", "https://mail.office.com", "https://mail.blogspot.com", "https://mail.aliexpress.com",
            "https://mail.zillow.com", "https://mail.hulu.com", "https://mail.spotify.com", "https://mail.dropbox.com",
            "https://m.google.com", "https://m.youtube.com", "https://m.facebook.com", "https://m.twitter.com",
            "https://m.instagram.com", "https://m.linkedin.com", "https://m.apple.com", "https://m.microsoft.com",
            "https://m.wikipedia.org", "https://m.amazon.com", "https://m.netflix.com", "https://m.yahoo.com",
            "https://m.bing.com", "https://m.reddit.com", "https://m.twitch.tv", "https://m.pinterest.com",
            "https://m.github.com", "https://m.stackoverflow.com", "https://m.bbc.co.uk", "https://m.cnn.com",
            "https://m.nytimes.com", "https://m.reuters.com", "https://m.weather.com", "https://m.imdb.com",
            "https://m.ebay.com", "https://m.walmart.com", "https://m.target.com", "https://m.chase.com",
            "https://m.bankofamerica.com", "https://m.wellsfargo.com", "https://m.paypal.com", "https://m.adobe.com",
            "https://m.wordpress.org", "https://m.office.com", "https://m.blogspot.com", "https://m.aliexpress.com",
            "https://m.zillow.com", "https://m.hulu.com", "https://m.spotify.com", "https://m.dropbox.com",
            "https://support.google.com", "https://support.youtube.com", "https://support.facebook.com", "https://support.twitter.com",
            "https://support.instagram.com", "https://support.linkedin.com", "https://support.apple.com", "https://support.microsoft.com",
            "https://support.wikipedia.org", "https://support.amazon.com", "https://support.netflix.com", "https://support.yahoo.com",
            "https://support.bing.com", "https://support.reddit.com", "https://support.twitch.tv", "https://support.pinterest.com",
            "https://support.github.com", "https://support.stackoverflow.com", "https://support.bbc.co.uk", "https://support.cnn.com",
            "https://support.nytimes.com", "https://support.reuters.com", "https://support.weather.com", "https://support.imdb.com",
            "https://support.ebay.com", "https://support.walmart.com", "https://support.target.com", "https://support.chase.com",
            "https://support.bankofamerica.com", "https://support.wellsfargo.com", "https://support.paypal.com", "https://support.adobe.com",
            "https://support.wordpress.org", "https://support.office.com", "https://support.blogspot.com", "https://support.aliexpress.com",
            "https://support.zillow.com", "https://support.hulu.com", "https://support.spotify.com", "https://support.dropbox.com"
        );

        List<String> phishingUrls = Arrays.asList(
            "http://secure-login-update-paypal.com", "http://secure-login-update-apple.com",
            "http://secure-login-update-microsoft.com", "http://secure-login-update-amazon.com",
            "http://secure-login-update-chase.com", "http://secure-login-update-netflix.com",
            "http://secure-login-update-facebook.com", "http://secure-login-update-instagram.com",
            "http://secure-login-update-twitter.com", "http://secure-login-update-linkedin.com",
            "http://secure-login-update-yahoo.com", "http://secure-login-update-gmail.com",
            "http://secure-login-update-office365.com", "http://secure-login-update-dropbox.com",
            "http://secure-login-update-google-drive.com", "http://secure-login-update-dhl.com",
            "http://secure-login-update-fedex.com", "http://secure-login-update-ups.com",
            "http://secure-login-update-steam.com", "http://secure-login-update-discord.com",
            "http://verify-account-paypal.com", "http://verify-account-apple.com",
            "http://verify-account-microsoft.com", "http://verify-account-amazon.com",
            "http://verify-account-chase.com", "http://verify-account-netflix.com",
            "http://verify-account-facebook.com", "http://verify-account-instagram.com",
            "http://verify-account-twitter.com", "http://verify-account-linkedin.com",
            "http://verify-account-yahoo.com", "http://verify-account-gmail.com",
            "http://verify-account-office365.com", "http://verify-account-dropbox.com",
            "http://verify-account-google-drive.com", "http://verify-account-dhl.com",
            "http://verify-account-fedex.com", "http://verify-account-ups.com",
            "http://verify-account-steam.com", "http://verify-account-discord.com",
            "http://signin-online-paypal.com", "http://signin-online-apple.com",
            "http://signin-online-microsoft.com", "http://signin-online-amazon.com",
            "http://signin-online-chase.com", "http://signin-online-netflix.com",
            "http://signin-online-facebook.com", "http://signin-online-instagram.com",
            "http://signin-online-twitter.com", "http://signin-online-linkedin.com",
            "http://signin-online-yahoo.com", "http://signin-online-gmail.com",
            "http://signin-online-office365.com", "http://signin-online-dropbox.com",
            "http://signin-online-google-drive.com", "http://signin-online-dhl.com",
            "http://signin-online-fedex.com", "http://signin-online-ups.com",
            "http://signin-online-steam.com", "http://signin-online-discord.com",
            "http://security-alert-paypal.com", "http://security-alert-apple.com",
            "http://security-alert-microsoft.com", "http://security-alert-amazon.com",
            "http://security-alert-chase.com", "http://security-alert-netflix.com",
            "http://security-alert-facebook.com", "http://security-alert-instagram.com",
            "http://security-alert-twitter.com", "http://security-alert-linkedin.com",
            "http://security-alert-yahoo.com", "http://security-alert-gmail.com",
            "http://security-alert-office365.com", "http://security-alert-dropbox.com",
            "http://security-alert-google-drive.com", "http://security-alert-dhl.com",
            "http://security-alert-fedex.com", "http://security-alert-ups.com",
            "http://security-alert-steam.com", "http://security-alert-discord.com",
            "http://locked-update-paypal.com", "http://locked-update-apple.com",
            "http://locked-update-microsoft.com", "http://locked-update-amazon.com",
            "http://locked-update-chase.com", "http://locked-update-netflix.com",
            "http://locked-update-facebook.com", "http://locked-update-instagram.com",
            "http://locked-update-twitter.com", "http://locked-update-linkedin.com",
            "http://locked-update-yahoo.com", "http://locked-update-gmail.com",
            "http://locked-update-office365.com", "http://locked-update-dropbox.com",
            "http://locked-update-google-drive.com", "http://locked-update-dhl.com",
            "http://locked-update-fedex.com", "http://locked-update-ups.com",
            "http://locked-update-steam.com", "http://locked-update-discord.com",
            "http://billing-update-secure-paypal.com", "http://billing-update-secure-apple.com",
            "http://billing-update-secure-microsoft.com", "http://billing-update-secure-amazon.com",
            "http://billing-update-secure-chase.com", "http://billing-update-secure-netflix.com",
            "http://billing-update-secure-facebook.com", "http://billing-update-secure-instagram.com",
            "http://billing-update-secure-twitter.com", "http://billing-update-secure-linkedin.com",
            "http://billing-update-secure-yahoo.com", "http://billing-update-secure-gmail.com",
            "http://billing-update-secure-office365.com", "http://billing-update-secure-dropbox.com",
            "http://billing-update-secure-google-drive.com", "http://billing-update-secure-dhl.com",
            "http://billing-update-secure-fedex.com", "http://billing-update-secure-ups.com",
            "http://billing-update-secure-steam.com", "http://billing-update-secure-discord.com",
            "http://login-help-paypal.com", "http://login-help-apple.com",
            "http://login-help-microsoft.com", "http://login-help-amazon.com",
            "http://login-help-chase.com", "http://login-help-netflix.com",
            "http://login-help-facebook.com", "http://login-help-instagram.com",
            "http://login-help-twitter.com", "http://login-help-linkedin.com",
            "http://login-help-yahoo.com", "http://login-help-gmail.com",
            "http://login-help-office365.com", "http://login-help-dropbox.com",
            "http://login-help-google-drive.com", "http://login-help-dhl.com",
            "http://login-help-fedex.com", "http://login-help-ups.com",
            "http://login-help-steam.com", "http://login-help-discord.com",
            "http://verify-badge-login-paypal.com", "http://verify-badge-login-apple.com",
            "http://verify-badge-login-microsoft.com", "http://verify-badge-login-amazon.com",
            "http://verify-badge-login-chase.com", "http://verify-badge-login-netflix.com",
            "http://verify-badge-login-facebook.com", "http://verify-badge-login-instagram.com",
            "http://verify-badge-login-twitter.com", "http://verify-badge-login-linkedin.com",
            "http://verify-badge-login-yahoo.com", "http://verify-badge-login-gmail.com",
            "http://verify-badge-login-office365.com", "http://verify-badge-login-dropbox.com",
            "http://verify-badge-login-google-drive.com", "http://verify-badge-login-dhl.com",
            "http://verify-badge-login-fedex.com", "http://verify-badge-login-ups.com",
            "http://verify-badge-login-steam.com", "http://verify-badge-login-discord.com",
            "http://support-secure-paypal.com", "http://support-secure-apple.com",
            "http://support-secure-microsoft.com", "http://support-secure-amazon.com",
            "http://support-secure-chase.com", "http://support-secure-netflix.com",
            "http://support-secure-facebook.com", "http://support-secure-instagram.com",
            "http://support-secure-twitter.com", "http://support-secure-linkedin.com",
            "http://support-secure-yahoo.com", "http://support-secure-gmail.com",
            "http://support-secure-office365.com", "http://support-secure-dropbox.com",
            "http://support-secure-google-drive.com", "http://support-secure-dhl.com",
            "http://support-secure-fedex.com", "http://support-secure-ups.com",
            "http://support-secure-steam.com", "http://support-secure-discord.com",
            "http://job-offer-secure-paypal.com", "http://job-offer-secure-apple.com",
            "http://job-offer-secure-microsoft.com", "http://job-offer-secure-amazon.com",
            "http://job-offer-secure-chase.com", "http://job-offer-secure-netflix.com",
            "http://job-offer-secure-facebook.com", "http://job-offer-secure-instagram.com",
            "http://job-offer-secure-twitter.com", "http://job-offer-secure-linkedin.com",
            "http://job-offer-secure-yahoo.com", "http://job-offer-secure-gmail.com",
            "http://job-offer-secure-office365.com", "http://job-offer-secure-dropbox.com",
            "http://job-offer-secure-google-drive.com", "http://job-offer-secure-dhl.com",
            "http://job-offer-secure-fedex.com", "http://job-offer-secure-ups.com",
            "http://job-offer-secure-steam.com", "http://job-offer-secure-discord.com"
        );

        try (PrintWriter writer = new PrintWriter(new FileWriter("src/main/resources/dataset.arff"))) {
            writer.println("@relation PhishingDataset");
            writer.println();
            writer.println("@attribute urlLength numeric");
            writer.println("@attribute numSuspiciousChars numeric");
            writer.println("@attribute numLexicalKeywords numeric");
            writer.println("@attribute hasSuspiciousIframes numeric");
            writer.println("@attribute externalLinkRatio numeric");
            writer.println("@attribute hasFakeLoginForm numeric");
            writer.println("@attribute class {benign, phishing}");
            writer.println();
            writer.println("@data");

            for (String url : benignUrls) {
                UrlFeatures f = featureExtractor.extractFeatures(url);
                writer.printf(Locale.US, "%.1f,%.1f,%.1f,%.1f,%.2f,%.1f,benign%n",
                    f.getUrlLength(), f.getNumSuspiciousChars(), f.getNumLexicalKeywords(),
                    f.getHasSuspiciousIframes(), f.getExternalLinkRatio(), f.getHasFakeLoginForm());
                System.out.println("Processed benign: " + url);
            }

            for (String url : phishingUrls) {
                UrlFeatures f = featureExtractor.extractFeatures(url);
                writer.printf(Locale.US, "%.1f,%.1f,%.1f,%.1f,%.2f,%.1f,phishing%n",
                    f.getUrlLength(), f.getNumSuspiciousChars(), f.getNumLexicalKeywords(),
                    f.getHasSuspiciousIframes(), f.getExternalLinkRatio(), f.getHasFakeLoginForm());
                System.out.println("Processed phishing: " + url);
            }
        }
        System.out.println("Dataset successfully generated at src/main/resources/dataset.arff");
    }
}
