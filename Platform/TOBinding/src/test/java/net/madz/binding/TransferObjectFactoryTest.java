package net.madz.binding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.madz.binding.TransferObjectFactory;
import net.madz.binding.annotation.AccessTypeEnum;
import net.madz.binding.annotation.Binding;
import net.madz.binding.annotation.BindingTypeEnum;

import org.junit.Test;

public class TransferObjectFactoryTest {

    @Test
    public void testCreateTransferObject_WithBothNullInputParams() {
        try {
            TransferObjectFactory.createTransferObject(null, null);
        } catch (Exception e) {
            assertEquals(NullPointerException.class, e.getClass());
        }
    }

    @Test
    public void testCreateTransferObject_WithFirstNullInputParam() {
        try {
            TransferObjectFactory.createTransferObject(null, new String());
        } catch (Exception e) {
            assertEquals(NullPointerException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testCreateTransferObject_WithSecondNullInputParam() {
        try {
            TransferObjectFactory.createTransferObject(String.class, null);
        } catch (Exception e) {
            assertEquals(NullPointerException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testCreateTransferObject_WithBothInputParamsWithNoField() {
        try {
            TransferObjectFactory.createTransferObject(NullFieldsClass.class, new NullFieldsClass());
        } catch (Exception e) {
            assertEquals(InstantiationException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testCreateTransferObject_WithFirstInputParamWithNoField() {
        try {
            TransferObjectFactory.createTransferObject(NullFieldsClass.class, new String());
        } catch (Exception e) {
            assertEquals(InstantiationException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testCreateTransferObject_WithSecondInputParamWithNoField() {
        try {
            TransferObjectFactory.createTransferObject(String.class, new NullFieldsClass());
        } catch (Exception e) {
            assertEquals(InstantiationException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testCreateTransferObject_WithSimpleBOSimpleTOClass() {
        WarehouseBase bizObject = new WarehouseBase();
        WarehouseTO actualTO = null;
        WarehouseTO expectedTO = new WarehouseTO();
        try {
            bizObject.id = 1L;
            bizObject.name = "John";
            bizObject.location = "Beijing";
            bizObject.description = "John is short for John is a ";
            expectedTO.name = bizObject.name;
            expectedTO.location = bizObject.location;
            expectedTO.description = bizObject.description;
            actualTO = TransferObjectFactory.createTransferObject(WarehouseTO.class, bizObject);
            assertEquals(expectedTO, actualTO);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testCreateTransferObject_WithInheritClassRelationAndBinding() {
        Warehouse bizObject = new Warehouse();
        WarehouseCTO expectedCTO = new WarehouseCTO();
        WarehouseCTO actualCTO = null;
        try {
            bizObject.id = 1L;
            bizObject.name = "John";
            bizObject.location = "Beijing";
            bizObject.description = "John is short for John is a .";
            WarehouseType wt = new WarehouseType();
            wt.id = 2L;
            wt.name = "full house";
            wt.description = "Store food";
            bizObject.type = wt;
            expectedCTO.name = bizObject.name;
            expectedCTO.location = bizObject.location;
            expectedCTO.description = bizObject.description;
            expectedCTO.typeId = bizObject.type.id;
            expectedCTO.typeName = bizObject.type.name;
            actualCTO = TransferObjectFactory.createTransferObject(WarehouseCTO.class, bizObject);
            System.out.println(actualCTO.name);
            System.out.println(actualCTO.location);
            assertEquals(expectedCTO, actualCTO);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateTransferObject_WithSimpleTOFieldsNotInSimpleBOFields() {
        WarehouseBase_Second bizObject = new WarehouseBase_Second();
        WarehouseTO_Second expectedTO = new WarehouseTO_Second();
        WarehouseTO_Second actualTO = null;
        bizObject.id = 1L;
        expectedTO.name = bizObject.name = "John";
        expectedTO.location = bizObject.location = "Beijing";
        expectedTO.description = bizObject.description = "John is short for John is a .";
        expectedTO.year = null;
        try {
            actualTO = TransferObjectFactory.createTransferObject(WarehouseTO_Second.class, bizObject);
            assertEquals(expectedTO, actualTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateTransferObject_WithBindingInTOButNotInBOFields() {
        Warehouse_Second bizObject = new Warehouse_Second();
        WarehouseCTO_Second expectedTO = new WarehouseCTO_Second();
        WarehouseCTO_Second actualTO = null;
        bizObject.id = 1L;
        bizObject.name = "John";
        bizObject.location = "Beijing";
        bizObject.description = "testCreateTransferObject_WithBindingInTOButNotInBOFields";
        bizObject.type = "store food";
        try {
            actualTO = TransferObjectFactory.createTransferObject(WarehouseCTO_Second.class, bizObject);
        } catch (Exception e) {
            assertEquals(NoSuchFieldException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testAssembleTransferObjectList_WithBothNullInputParams() {
        try {
            TransferObjectFactory.assembleTransferObjectList(null, null);
        } catch (Exception e) {
            assertEquals(NullPointerException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testAssembleTransferObjectList_WithFirstNullInputParam() {
        try {
            TransferObjectFactory.assembleTransferObjectList(null, String.class);
        } catch (Exception e) {
            assertEquals(NullPointerException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testAssembleTransferObjectList_WithSecondNullInputParam() {
        try {
            List bizObject = new ArrayList();
            bizObject.add(new String("aa"));
            bizObject.add(new String("bb"));
            TransferObjectFactory.assembleTransferObjectList(bizObject, null);
        } catch (Exception e) {
            assertEquals(NullPointerException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testAssembleTransferObjectList_WithSimpleTO() {
        List<WarehouseBase> bizObjList = new ArrayList<WarehouseBase>();
        WarehouseBase w1 = new WarehouseBase();
        w1.id = 1L;
        w1.name = "John";
        w1.location = "Beijing";
        w1.description = "";
        WarehouseBase w2 = new WarehouseBase();
        w2.id = 2L;
        w2.name = "John";
        w2.location = "Beijing";
        w2.description = "";
        bizObjList.add(w1);
        bizObjList.add(w2);
        List<WarehouseTO> expectedTOList = new ArrayList<WarehouseTO>();
        WarehouseTO t1 = new WarehouseTO();
        t1.name = "John";
        t1.location = "Beijing";
        t1.description = "";
        WarehouseTO t2 = new WarehouseTO();
        t2.name = "John";
        t2.location = "Beijing";
        t2.description = "";
        expectedTOList.add(t1);
        expectedTOList.add(t2);
        List<WarehouseTO> actualTOList = null;
        try {
            actualTOList = TransferObjectFactory.assembleTransferObjectList(bizObjList, WarehouseTO.class);
            assertEquals(expectedTOList, actualTOList);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testAssembleTransferObjectList_WithSimpleTOButWithTOFieldNotInBO() {
        List<WarehouseBase_Second> bizObjList = new ArrayList<WarehouseBase_Second>();
        WarehouseBase_Second w1 = new WarehouseBase_Second();
        w1.id = 1L;
        w1.name = "John";
        w1.location = "Beijing";
        w1.description = "";
        WarehouseBase_Second w2 = new WarehouseBase_Second();
        w2.id = 2L;
        w2.name = "John";
        w2.location = "Beijing";
        w2.description = "";
        bizObjList.add(w1);
        bizObjList.add(w2);
        List<WarehouseTO_Second> expectedTOList = new ArrayList<WarehouseTO_Second>();
        WarehouseTO_Second t1 = new WarehouseTO_Second();
        t1.name = "John";
        t1.location = "Beijing";
        t1.description = "";
        t1.year = null;
        WarehouseTO_Second t2 = new WarehouseTO_Second();
        t2.name = "John";
        t2.location = "Beijing";
        t2.description = "";
        t2.year = null;
        expectedTOList.add(t1);
        expectedTOList.add(t2);
        List<WarehouseTO_Second> actualTOList = null;
        try {
            actualTOList = TransferObjectFactory.assembleTransferObjectList(bizObjList, WarehouseTO_Second.class);
            assertEquals(expectedTOList, actualTOList);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testAssembleCompositeSingleValueTO() {
        Parent p = new Parent();
        Child c = new Child();
        c.name = "Child";
        p.child = c;
        p.name = "Parent";
        try {
            ParentTO actualResult = TransferObjectFactory.createTransferObject(ParentTO.class, p);
            assert ( actualResult.child.name.equals("Child") );
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testAssembleCompositeListValueTO() {
        ParentWithChildren p = new ParentWithChildren();
        p.id = 1L;
        p.name = "Parent";
        Child c = new Child();
        c.name = "Child1";
        c.id = 1L;
        p.children.add(c);
        c = new Child();
        c.name = "Child2";
        c.id = 2L;
        p.children.add(c);
        c = new Child();
        c.name = "Child3";
        c.id = 3L;
        p.children.add(c);
        try {
            ParentWithListChildrenTO actualResult = TransferObjectFactory.createTransferObject(
                    ParentWithListChildrenTO.class, p);
            assert ( actualResult.children.size() == 3 );
            for ( int i = 0; i < 3; i++ ) {
                assert ( actualResult.children.get(i).name.equals("Child" + ( i + 1 )) );
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testAssembleCompositeSetValueTO() {
        ParentWithChildren p = new ParentWithChildren();
        p.name = "Parent";
        Child c = new Child();
        c.name = "Child";
        p.children.add(c);
        c = new Child();
        c.name = "Child";
        p.children.add(c);
        c = new Child();
        c.name = "Child";
        p.children.add(c);
        try {
            ParentWithSetChildrenTO actualResult = TransferObjectFactory.createTransferObject(
                    ParentWithSetChildrenTO.class, p);
            assert ( actualResult.children.size() == 3 );
            for ( ChildTO child : actualResult.children ) {
                assert ( child.name.equals("Child") );
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testAssembleCompositeSetValuePropertyAccessTO() {
        ParentWithChildren p = new ParentWithChildren();
        p.name = "Parent";
        Child c = new Child();
        c.name = "Child";
        p.children.add(c);
        c = new Child();
        c.name = "Child";
        p.children.add(c);
        c = new Child();
        c.name = "Child";
        p.children.add(c);
        try {
            ParentWithSetChildrenPropertyAccessTO actualResult = TransferObjectFactory.createTransferObject(
                    ParentWithSetChildrenPropertyAccessTO.class, p);
            assert ( actualResult.children.size() == 3 );
            for ( ChildTO child : actualResult.children ) {
                assert ( child.name.equals("Child") );
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private class NullFieldsClass {}

    public static class WarehouseBase implements Serializable {

        // private static final long serialVersionUID = 1L;
        protected Long id;

        protected String name;

        protected String description;

        protected String location;
    }

    public static class Warehouse extends WarehouseBase {

        protected WarehouseType type;
    }

    public static class WarehouseTO implements Serializable {

        // private static final long serialVersionUID = 1L;
        protected String name;

        protected String location;

        protected String description;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( ( description == null ) ? 0 : description.hashCode() );
            result = prime * result + ( ( location == null ) ? 0 : location.hashCode() );
            result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) {
                return true;
            }
            if ( obj == null ) {
                return false;
            }
            if ( getClass() != obj.getClass() ) {
                return false;
            }
            WarehouseTO other = (WarehouseTO) obj;
            if ( description == null ) {
                if ( other.description != null ) {
                    return false;
                }
            } else if ( !description.equals(other.description) ) {
                return false;
            }
            if ( location == null ) {
                if ( other.location != null ) {
                    return false;
                }
            } else if ( !location.equals(other.location) ) {
                return false;
            }
            if ( name == null ) {
                if ( other.name != null ) {
                    return false;
                }
            } else if ( !name.equals(other.name) ) {
                return false;
            }
            return true;
        }
    }

    public static class WarehouseCTO extends WarehouseTO {

        @Binding(name = "type.id")
        protected Long typeId;

        @Binding(name = "type.name")
        protected String typeName;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + ( ( typeId == null ) ? 0 : typeId.hashCode() );
            result = prime * result + ( ( typeName == null ) ? 0 : typeName.hashCode() );
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( !super.equals(obj) ) return false;
            if ( getClass() != obj.getClass() ) return false;
            WarehouseCTO other = (WarehouseCTO) obj;
            if ( typeId == null ) {
                if ( other.typeId != null ) return false;
            } else if ( !typeId.equals(other.typeId) ) return false;
            if ( typeName == null ) {
                if ( other.typeName != null ) return false;
            } else if ( !typeName.equals(other.typeName) ) return false;
            return true;
        }
    }

    public class WarehouseType implements Serializable {

        private static final long serialVersionUID = 1L;

        protected Long id;

        protected String name;

        protected String description;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ( ( description == null ) ? 0 : description.hashCode() );
            result = prime * result + ( ( id == null ) ? 0 : id.hashCode() );
            result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            WarehouseType other = (WarehouseType) obj;
            if ( !getOuterType().equals(other.getOuterType()) ) return false;
            if ( description == null ) {
                if ( other.description != null ) return false;
            } else if ( !description.equals(other.description) ) return false;
            if ( id == null ) {
                if ( other.id != null ) return false;
            } else if ( !id.equals(other.id) ) return false;
            if ( name == null ) {
                if ( other.name != null ) return false;
            } else if ( !name.equals(other.name) ) return false;
            return true;
        }

        private TransferObjectFactoryTest getOuterType() {
            return TransferObjectFactoryTest.this;
        }
    }

    public static class WarehouseBase_Second implements Serializable {

        private static final long serialVersionUID = 1L;

        protected Long id;

        protected String name;

        protected String description;

        protected String location;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( ( description == null ) ? 0 : description.hashCode() );
            result = prime * result + ( ( id == null ) ? 0 : id.hashCode() );
            result = prime * result + ( ( location == null ) ? 0 : location.hashCode() );
            result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            WarehouseBase_Second other = (WarehouseBase_Second) obj;
            if ( description == null ) {
                if ( other.description != null ) return false;
            } else if ( !description.equals(other.description) ) return false;
            if ( id == null ) {
                if ( other.id != null ) return false;
            } else if ( !id.equals(other.id) ) return false;
            if ( location == null ) {
                if ( other.location != null ) return false;
            } else if ( !location.equals(other.location) ) return false;
            if ( name == null ) {
                if ( other.name != null ) return false;
            } else if ( !name.equals(other.name) ) return false;
            return true;
        }
    }

    public static class WarehouseTO_Second implements Serializable {

        private static final long serialVersionUID = 1L;

        protected Long id;

        protected String name;

        protected String location;

        protected String description;

        protected String year;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( ( description == null ) ? 0 : description.hashCode() );
            result = prime * result + ( ( location == null ) ? 0 : location.hashCode() );
            result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
            result = prime * result + ( ( year == null ) ? 0 : year.hashCode() );
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            WarehouseTO_Second other = (WarehouseTO_Second) obj;
            if ( description == null ) {
                if ( other.description != null ) return false;
            } else if ( !description.equals(other.description) ) return false;
            if ( location == null ) {
                if ( other.location != null ) return false;
            } else if ( !location.equals(other.location) ) return false;
            if ( name == null ) {
                if ( other.name != null ) return false;
            } else if ( !name.equals(other.name) ) return false;
            if ( year == null ) {
                if ( other.year != null ) return false;
            } else if ( !year.equals(other.year) ) return false;
            return true;
        }
    }

    public static class Warehouse_Second extends WarehouseBase_Second {

        private static final long serialVersionUID = 1L;

        protected String type;
    }

    public static class WarehouseCTO_Second extends WarehouseCTO {

        private static final long serialVersionUID = 1L;

        @Binding(name = "type.id")
        protected Long typeId;

        @Binding(name = "type.name")
        protected String typeName;
    }

    public static class Parent {

        protected Long id;

        protected String name;

        protected Child child;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Child {

        protected Long id;

        protected String name;
    }

    public static class ParentTO implements Serializable {

        protected String name;

        @Binding(bindingType = BindingTypeEnum.Entity, embeddedType = ChildTO.class)
        protected ChildTO child;
    }

    public static class ChildTO implements Serializable {

        protected String name;
    }

    public static class ParentWithChildren {

        protected Long id;

        protected String name;

        protected final List<Child> children = new ArrayList<Child>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Child> getChildren() {
            return children;
        }
    }

    public static class ParentWithListChildrenTO {

        protected Long id;

        protected String name;

        @Binding(bindingType = BindingTypeEnum.Entity, embeddedType = ChildTO.class)
        protected List<ChildTO> children;
    }

    public static class ParentWithSetChildrenTO {

        protected String name;

        @Binding(bindingType = BindingTypeEnum.Entity, embeddedType = ChildTO.class)
        protected Set<ChildTO> children;
    }

    public static class ParentWithSetChildrenPropertyAccessTO {

        protected String name;

        @Binding(bindingType = BindingTypeEnum.Entity, embeddedType = ChildTO.class,
                accessType = AccessTypeEnum.Property)
        protected Set<ChildTO> children;
    }
}
