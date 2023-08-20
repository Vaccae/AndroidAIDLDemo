// ITestDataAidlInterface.aidl
package vac.test.aidlservice;

// Declare any non-default types here with import statements

parcelable TestData;

interface ITestDataAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);


    //根据编码返回测试类
    TestData getTestData(String code);

    //返回测试列表
    List<TestData> getTestDatas();

    //修改测试类
    boolean updateTestData(in TestData data);
}