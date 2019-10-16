package aggregator.model;

import aggregator.vo.Vacancy;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HHStrategy implements Strategy {
    private static final String URL_FORMAT = "http://hh.ua/search/vacancy?text=java+%s&page=%d";

    @Override
    public List<Vacancy> getVacancies(String searchString) {
        List<Vacancy> vacancies = new ArrayList<>();
        boolean hasVacancies;
        int page = 0;
        do {
            Document doc;
            try {
                doc = getDocument(searchString, page);
            } catch (IOException ignored) {
                break;
            }
            //Elements divElements = doc.select("div[data-qa=\"vacancy-serp__vacancy\"]");
            Elements divElements = doc.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy");
            if (hasVacancies = divElements.size() > 0) {
                for (Element e : divElements) {
                    Vacancy vacancy = new Vacancy();
                    vacancy.setTitle(e.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-title").text().trim());
                    vacancy.setUrl(e.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-title").attr("href").trim());
                    vacancy.setCity(e.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-address").text().trim());
                    vacancy.setCompanyName(e.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-employer").text().trim());
                    vacancy.setSiteName("http://hh.ua");
                    vacancy.setSalary(e.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-compensation").text().trim());
                    vacancies.add(vacancy);
                }
                page++;
            }
        } while (hasVacancies);
        return vacancies;
    }

    protected Document getDocument(String searchString, int page) throws IOException {
        String url = String.format(URL_FORMAT, searchString, page);
        Connection connection = Jsoup.connect(url);
        connection.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");
        connection.referrer("");
        Document document = connection.get();
        return document;
    }
}
