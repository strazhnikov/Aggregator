package aggregator;

import aggregator.model.HHStrategy;
import aggregator.model.Model;
import aggregator.model.Provider;
import aggregator.view.HtmlView;
import aggregator.view.View;

//TODO: 1. correcting query according to new query format on website

//TODO: 2. implement the ability to enter simple search criteria

//TODO: 3. implement additional strategies, i.e. work.ua, robota.ua, jobs.dou.ua

public class Aggregator {
    public static void main(String[] args) {
        View view = new HtmlView();
        Model model = new Model(view, new Provider(new HHStrategy()));
        Controller controller = new Controller(model);
        view.setController(controller);
        ((HtmlView) view).userCitySelectEmulationMethod();
    }
}
