package kmfsample;

import org.kevoree.modeling.KCallback;
import org.kevoree.modeling.KModel;
import org.kevoree.modeling.KObject;
import org.kevoree.modeling.memory.manager.DataManagerBuilder;
import org.kevoree.modeling.meta.KMetaClass;
import org.kevoree.modeling.meta.KMetaModel;
import org.kevoree.modeling.meta.KPrimitiveTypes;
import org.kevoree.modeling.meta.impl.MetaModel;
import org.kevoree.modeling.scheduler.impl.DirectScheduler;
import org.kevoree.modeling.traversal.visitor.KVisitResult;

public class App {

    public static final long BASE_UNIVERSE = 0;

    public static final long BASE_TIME = 0;

    public static void main(String[] args) {

        KMetaModel metaModel = new MetaModel("SmartCityMetaModel");
        KMetaClass metaClassCity = metaModel.addMetaClass("City");
        KMetaClass metaClassDistrict = metaModel.addMetaClass("District");

        metaClassCity.addAttribute("name", KPrimitiveTypes.STRING);

        metaClassDistrict.addAttribute("name", KPrimitiveTypes.STRING);
        metaClassDistrict.addAttribute("nbcitizen", KPrimitiveTypes.LONG);

        //create the reference districts from City to district with multiplicity 0..*
        metaClassCity.addRelation("districts", metaClassDistrict, null);

        KModel model = metaModel.createModel(DataManagerBuilder.create().withScheduler(new DirectScheduler()).build());
        model.connect(o -> {

            //Create reflexively a model object using the metaClass name
            KObject city = model.createByName("City", BASE_UNIVERSE, BASE_TIME);
            city.setByName("name", "MySmartCity");

            //Create reflexively a model object using the metaClass
            KObject district_1 = model.create(metaClassDistrict, BASE_UNIVERSE, BASE_TIME);
            district_1.setByName("name", "District_1");
            district_1.setByName("nbcitizen", 10000);

            //Create reflexively a model object using the metaClass
            KObject district_2 = model.createByName("District", BASE_UNIVERSE, BASE_TIME);
            district_2.setByName("name", "District_2");
            district_2.setByName("nbcitizen", 50000);

            //Add the two district to the City
            city.addByName("districts", district_1);
            city.addByName("districts", district_2);

            //Save the full model as JSON in the console
            model.universe(BASE_UNIVERSE).time(BASE_TIME).json().save(city, System.out::println);

            //Visiting all reachable objects from the city
            city.visit(elem -> {
                System.out.println("Visiting..." + elem.toJSON());
                return KVisitResult.CONTINUE;
            }, o1 -> System.out.println("End of the visit"));

            //Visiting all attributes of an object
            city.visitAttributes((metaAttribute, value) -> {
                System.out.println("City attribute " + metaAttribute.metaName() + ", type=" + metaAttribute.attributeTypeId() + "=" + value);
            });

            //Finally any object have a UUID and can be retrieve from it
            long cityUUID = city.uuid();
            System.out.println("City uuid=" + cityUUID);
            model.lookup(BASE_UNIVERSE, BASE_TIME, cityUUID, new KCallback<KObject>() {
                @Override
                public void on(KObject resolvedObject) {
                    System.out.println("Resolved=" + resolvedObject.toJSON());
                }
            });

        });

    }

}
