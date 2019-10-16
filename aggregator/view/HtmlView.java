package aggregator.view;

import aggregator.Controller;
import aggregator.vo.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class HtmlView implements View {
    private Controller controller;
    private final String filePath = "./src/" + this.getClass().getPackage().getName().replace('.', '/') + "/vacancies.html";

    @Override
    public void update(List<Vacancy> vacancies) {
        System.out.println(vacancies.size());
        try {
            String content = getUpdatedFileContent(vacancies);
            if (content == null) {
                System.out.println("Can't processing vacancies.html");
            } else {
                updateFile(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void userCitySelectEmulationMethod() {
        controller.onCitySelect("Львов");
    }

    private String getUpdatedFileContent(List<Vacancy> vacancies) {
        Document doc;
        try {
            doc = getDocument();
        } catch (IOException e) {
            e.printStackTrace();
            return "Some exception occurred";
        }
        if (doc == null) return null;

        // get template <TR> tag element
        Element originTemplate = doc.getElementsByClass("template").first();
        Element vacancyTemplate = originTemplate.clone();
        vacancyTemplate.removeClass("template").removeAttr("style");

        // delete vacancies from HTML file
        Elements deleteElements = doc.getElementsByClass("vacancy");
        for (Element e : deleteElements) {
            if (e.hasClass("template")) continue;
            e.remove();
        }

        // prepare new vacancies list for inserting into HTML file
        for (Vacancy vacancy : vacancies) {
            Element vacancyElement = vacancyTemplate.clone();

            vacancyElement.getElementsByClass("city").first().text(vacancy.getCity());
            vacancyElement.getElementsByClass("companyName").first().text(vacancy.getCompanyName());
            vacancyElement.getElementsByClass("salary").first().text(vacancy.getSalary());
            Element titleElement = vacancyElement.getElementsByTag("a").first();
            titleElement.text(vacancy.getTitle());
            titleElement.attr("href", vacancy.getUrl());

            originTemplate.before(vacancyElement.outerHtml());
        }

        return doc.outerHtml();
    }

    private void updateFile(String fileContent) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(fileContent);
        writer.flush();
        writer.close();

        // make backup
        Path source = Paths.get(filePath);
        Path target = source.getParent().resolve("backup.html");
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    }

    protected Document getDocument() throws IOException {
        return Jsoup.parse(new File(filePath), "UTF-8");
    }
}
