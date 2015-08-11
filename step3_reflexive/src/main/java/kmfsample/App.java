package kmfsample;

import org.kevoree.modeling.KModel;
import org.kevoree.modeling.KObject;
import org.kevoree.modeling.memory.manager.DataManagerBuilder;
import org.kevoree.modeling.meta.KMetaClass;
import org.kevoree.modeling.meta.KMetaModel;
import org.kevoree.modeling.meta.KPrimitiveTypes;
import org.kevoree.modeling.meta.impl.MetaModel;

public class App {

    public static final long BASE_UNIVERSE = 0;

    public static final long BASE_TIME = 0;

    public static void printObjects(Object[] objs) {
        System.out.println("ResultSize:" + objs.length);
        for (Object obj : objs) {
            if (obj instanceof KObject) {
                System.out.println(((KObject) obj).toJSON());
            } else {
                System.out.println(obj);
            }
        }
    }

    public static void main(String[] args) {

        KMetaModel metaModel = new MetaModel("SmartCityMetaModel");
        KMetaClass metaClassCity = metaModel.addMetaClass("City");
        metaClassCity.addAttribute("name", KPrimitiveTypes.STRING);

        KMetaClass metaClassDistrict = metaModel.addMetaClass("District");
        metaClassDistrict.addAttribute("name", KPrimitiveTypes.STRING);
        //create the reference districts from City to district with multiplicity 0..*
        metaClassCity.addReference("districts", metaClassDistrict, null, true);

        KModel model = metaModel.createModel(DataManagerBuilder.buildDefault());
        model.connect(o -> {

            //Create reflexively a model object using the metaClass name
            KObject city = model.createByName("City", BASE_UNIVERSE, BASE_TIME);
            city.setByName("name", "MySmartCity");

            //Create reflexively a model object using the metaClass
            KObject district_1 = model.create(metaClassDistrict, BASE_UNIVERSE, BASE_TIME);
            district_1.setByName("name", "District_1");

            //Create reflexively a model object using the metaClass
            KObject district_2 = model.createByName("District", BASE_UNIVERSE, BASE_TIME);
            district_1.setByName("name", "District_1");

            city.m

        });

    }

}
