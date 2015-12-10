package kmfsample;

import org.kevoree.modeling.memory.manager.DataManagerBuilder;
import org.kevoree.modeling.scheduler.impl.DirectScheduler;
import smartcity.City;
import smartcity.District;
import smartcity.SmartcityModel;
import smartcity.SmartcityView;

public class App {

    public static final long BASE_UNIVERSE = 0;
    public static final long BASE_TIME = 0;

    public static void main(String[] args) {

        final SmartcityModel model = new SmartcityModel(DataManagerBuilder.create().withScheduler(new DirectScheduler()).build());

        model.connect(o -> {

            SmartcityView baseView = model.universe(BASE_UNIVERSE).time(BASE_TIME);

            City city = baseView.createCity();
            city.setName("MySmartCity");
            District newDistrict_1 = baseView.createDistrict();
            newDistrict_1.setName("District_1");
            city.addDistricts(newDistrict_1);

            SmartcityView t7View = model.universe(BASE_UNIVERSE).time(7);
            District district_2_t7 = t7View.createDistrict();
            district_2_t7.setName("district_2_t7");

            SmartcityView t12View = model.universe(BASE_UNIVERSE).time(12);
            t12View.lookup(city.uuid(), city_t12 -> {

                t12View.lookup(district_2_t7.uuid(), district_2_t12 -> {
                    ((City) city_t12).addDistricts((District) district_2_t12);

                    SmartcityView t5View = model.universe(BASE_UNIVERSE).time(5);
                    t5View.lookup(district_2_t7.uuid(), district_2_t5 -> {
                        System.out.println(district_2_t5);
                    });

                    SmartcityView t8View = model.universe(BASE_UNIVERSE).time(8);
                    t8View.lookup(district_2_t7.uuid(), district_2_t8 -> {
                        System.out.println(district_2_t8);
                    });
                    t8View.lookup(city.uuid(), city_t8 -> {
                        System.out.println(city_t8);
                        System.out.println(((City) city_t8).sizeOfDistricts());
                    });

                    SmartcityView t13View = model.universe(BASE_UNIVERSE).time(13);
                    t13View.lookup(city.uuid(), city_t13 -> {
                        System.out.println(city_t13);
                        System.out.println(((City) city_t13).sizeOfDistricts());
                    });

                });

                city_t12.jump(0, city_t0 -> {
                    System.out.println(" -> " + city_t0);
                });
            });

        });
    }
}
