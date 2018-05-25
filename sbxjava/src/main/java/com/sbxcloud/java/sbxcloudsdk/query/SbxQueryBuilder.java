package com.sbxcloud.java.sbxcloudsdk.query;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.RawValue;
import com.sbxcloud.java.sbxcloudsdk.util.SbxDataValidator;



/**
 * Created by lgguzman on 18/02/17.
 */

public class SbxQueryBuilder {
    /**
     * @author https://archivo.digital
     *
     * Example
     *
     *   SELECT * from mi_modelo WHERE (profesion = 'cantante' OR
     *   profesion = 'guitarrista') AND (ganancias > 100000 AND ventas < 10)
     *
     *        SbxQueryBuilder queryBuilder = new SbxQueryBuilder(ApiConstants.DOMAIN,"mi_modelo");
     *            queryBuilder.whereIsEqual("profesion","cantante")
     *            .addOR()
     *            .whereIsEqual("profesion","guitarrista")
     *            .newGroup(ADQueryBuilder.ANDOR.AND)
     *            .whereGreaterThan("ganancias",100000)
     *            .addAND()   // by default is the last operator used or AND it is the first time
     *            .whereLessThan("ventas",10);
     *            ObjectNode jsonObject=queryBuilder.compile();
     *
     *    INSERT INTO mi_modelo (profesion,ganancias,ventas) VALUES ('cantante',30000,9)
     *
     *        SbxQueryBuilder queryBuilder = new SbxQueryBuilder(ApiConstants.DOMAIN,"mi_modelo", SbxQueryBuilder.TYPE.INSERT);
     *            queryBuilder.insertNewEmptyRow()
     *            .insertFieldLastRow("profesion","cantante")
     *            .insertFieldLastRow("ganancias",3000)
     *            .insertFieldLastRow("ventas",9);
     *            ObjectNode jsonObject=queryBuilder.compile();
     *
     *
     */


    private ArrayNode fetch;
    private ObjectNode obj;
    private ObjectNode referenceJoin;
    private ArrayNode required;
    private ArrayNode where;
    private ArrayNode keysD;
    private ObjectNode currentGroup;
    private ArrayNode row;
    private ObjectMapper mapper;
    public enum TYPE {
        INSERT, SELECT, DELETE
    }

    public enum ANDOR {
        AND, OR
    }

    private ANDOR lastANDOR=null;
    public enum OP {
        EQ("="),
        LIKE("LIKE"),
        IN("IN"),
        NOTIN("NOT IN"),
        LT("<"),
        GT(">"),
        LET("<="),
        GET(">="),
        NOT("<>"),
        IS("IS"),
        ISNOT("IS NOT");

        private String val;

        OP(String s) {
            this.val = s;
        }

        public String getValue() {
            return val;
        }
    }

    public ArrayNode getKeysD() {
        return keysD;
    }

    public SbxQueryBuilder(int domain, String rowModel) {
        init(domain, rowModel);
    }

    private void init(int domain, String rowModel) {
        mapper = new ObjectMapper();
        obj = mapper.createObjectNode();
        obj.put("domain", domain);
        obj.put("row_model", rowModel);
        required = mapper.createArrayNode();
        fetch = mapper.createArrayNode();
        where = mapper.createArrayNode();
        currentGroup = mapper.createObjectNode();
        currentGroup.put("ANDOR", ANDOR.AND.name());
        currentGroup.set("GROUP", mapper.createArrayNode());
    }



    public SbxQueryBuilder(int domain, String rowModel, TYPE type )  {
        switch (type){
            case SELECT:
                init(domain,rowModel);
                break;
            case INSERT:
                obj = mapper.createObjectNode();
                obj.put("domain", domain);
                obj.put("row_model", rowModel);
                row = mapper.createArrayNode();
                break;
            case DELETE:
                obj = mapper.createObjectNode();
                obj.put("domain", domain);
                obj.put("row_model", rowModel);
                keysD = mapper.createArrayNode();
                break;
        }

    }

    public SbxQueryBuilder addGeoSort(double lat, double lon, String latName, String lonName){

        try {
            ObjectNode jsonObject = mapper.createObjectNode();
            jsonObject.put("lat",lat);
            jsonObject.put("lon",lon);
            jsonObject.put("latProperty",latName);
            jsonObject.put("lonProperty",lonName);
            obj.set("geosort",jsonObject);
        }catch (Exception ex){}
        return this;
    }

    public boolean isInsert(){
        return row!=null && !row.isEmpty(null)  && row.get(0).get("_KEY").asText("").equals("");
    }

    public SbxQueryBuilder insert(ObjectNode jsonObject)throws  Exception{
        if(row!=null){
            row.add(jsonObject);
        }else{
            new Exception("it is not insert type");
        }
        return this;
    }

    public  SbxQueryBuilder insert(Object jsonObject)throws  Exception{
        if(row!=null){
            row.addPOJO(jsonObject);
        }else{
            new Exception("it is not insert type");
        }
        return this;
    }

    public SbxQueryBuilder insertNewEmptyRow()throws  Exception{
        if(row!=null){
            row.add(mapper.createObjectNode());
        }else{
            new Exception("it is not insert type");
        }
        return this;
    }

    public SbxQueryBuilder addDeleteKey(String key){
        if(keysD!=null){
            keysD.add(key);
        }
        return this;
    }


    public SbxQueryBuilder insertFieldAtRow(String field, Object data, int position)throws  Exception{
        if(row!=null){
            if(position < row.size())
                SbxDataValidator.putInJsonObject((ObjectNode) row.get(position),field,data) ;
            else
                new Exception("index Of Bound");
        }else{
            new Exception("it is not insert type");
        }
        return this;
    }

    public SbxQueryBuilder insertFieldFirstRow(String field, Object data)throws  Exception{
        int position=0;
        insertFieldAtRow(field,data,position);
        return this;
    }

    public SbxQueryBuilder insertFieldLastRow(String field, Object data)throws  Exception{
        int position=row.size()-1;
        insertFieldAtRow(field,data,position);
        return this;
    }

    public void setPage(int page)  {
        obj.put("page", page);
    }

    public int getPage(){
        return  obj.get("page").asInt(1);
    }

    public SbxQueryBuilder(int domain, String rowModel, int page, int size)  {
        init(domain,rowModel);
        obj.put("page", page);
        obj.put("size", size);
    }

    private SbxQueryBuilder newGroup(ANDOR andor)  {

        if(row==null) {
            lastANDOR=null;
            where.add(currentGroup);
            currentGroup = mapper.createObjectNode();
            currentGroup.put("ANDOR", andor.name());
            currentGroup.set("GROUP", mapper.createArrayNode());
        }
        return this;
    }

    public SbxQueryBuilder newGroupWithAnd()  {
        return newGroup(ANDOR.AND);
    }

    public SbxQueryBuilder newGroupWithOr()  {
        return newGroup(ANDOR.OR);
    }

    private SbxQueryBuilder addField(OP op, ANDOR andor, String field, Object value)  {
        if(row==null) {
            ObjectNode tmp = mapper.createObjectNode();
            if(currentGroup.get("GROUP").size() > 0)
                tmp.put("ANDOR", andor.toString());
            else
                tmp.put("ANDOR", ANDOR.AND.toString());
            tmp.put("FIELD", field);
            tmp.put("OP", op.getValue());
            SbxDataValidator.putInJsonObject(tmp,"VAL",value);
            ((ArrayNode) currentGroup.get("GROUP")).add(tmp);
        }
        return this;
    }

    private SbxQueryBuilder setReferenceJoin(OP op, String filterField,String referenceField, String model ,Object value)  {
        referenceJoin = mapper.createObjectNode();
        referenceJoin.put("row_model",model);
        ObjectNode filter = mapper.createObjectNode();
        filter.put("OP", op.getValue());
        SbxDataValidator.putInJsonObject(filter,"VAL",value);
        filter.put("FIELD",filterField);
        referenceJoin.put("reference_field",referenceField);
        referenceJoin.set("filter", filter);
        return this;
    }

    // PRIVATE QUERY METHODS

    private SbxQueryBuilder addAND(){
        lastANDOR = ANDOR.AND;
        return  this;
    }

    private SbxQueryBuilder addOR(){
        if (lastANDOR != null)
            lastANDOR = ANDOR.OR;
        else
            lastANDOR = ANDOR.AND;
        return  this;
    }

    private SbxQueryBuilder whereIsEqual(String field, Object value) {
        return   addField(OP.EQ,lastANDOR,field,value);
    }

    private SbxQueryBuilder whereIsNotNull(String field) {
        return   addField(OP.ISNOT,lastANDOR,field,null);
    }

    private SbxQueryBuilder whereIsNull(String field) {
        return   addField(OP.IS,lastANDOR,field,null);
    }

    private SbxQueryBuilder whereIn(String field, Object value) {
        return   addField(OP.IN,lastANDOR,field,value);
    }

    private SbxQueryBuilder whereNotIn(String field, Object value) {
        return   addField(OP.NOTIN,lastANDOR,field,value);
    }

    private SbxQueryBuilder whereGreaterThan(String field, Object value) {
        return   addField(OP.GT,lastANDOR,field,value);
    }

    private SbxQueryBuilder whereLessThan(String field, Object value) {
        return   addField(OP.LT,lastANDOR,field,value);
    }

    private SbxQueryBuilder whereGreaterOrEqualThan(String field, Object value) {
        return   addField(OP.GET,lastANDOR,field,value);
    }

    private SbxQueryBuilder whereLessOrEqualThan(String field, Object value) {
        return   addField(OP.LET,lastANDOR,field,value);
    }

    private SbxQueryBuilder whereIsNotEqual(String field, Object value) {
        return   addField(OP.NOT,lastANDOR,field,value);
    }

    private SbxQueryBuilder whereLike(String field, Object value) {
        return   addField(OP.LIKE,lastANDOR,field,value);
    }


    public class ReferenceJoin {

        private SbxQueryBuilder find;
        private String field;
        private String referenceField;

        ReferenceJoin(SbxQueryBuilder find, String field, String referenceField, String type) throws Exception {
            this.find = find;
            this.field = field;
            this.referenceField = referenceField;
            if (type.equals("AND")) {
                this.find.andWhereIn(this.field, "@reference_join@");
            } else {
                this.find.orWhereIn(this.field, "@reference_join@");
            }
        }


        public FilterJoin in(String referenceModel) {
            return new FilterJoin(this.find, this.field, this.referenceField, referenceModel);
        }
    }


    public class FilterJoin {

        private SbxQueryBuilder find;
        private String field;
        private String referenceField;
        private String referenceModel;


        FilterJoin(SbxQueryBuilder find,String field,String referenceField,String referenceModel) {
            this.find = find;
            this.field = field;
            this.referenceField = referenceField;
            this.referenceModel = referenceModel;
        }


        public SbxQueryBuilder filterWhereIsEqual(String field, Object value) throws Exception{
            this.find.setReferenceJoin(OP.EQ, field, this.referenceField, this.referenceModel, value);
            return this.find;
        }

        public SbxQueryBuilder filterWhereIsNotNull(String field) throws  Exception{
            this.find.setReferenceJoin(OP.ISNOT, field, this.referenceField, this.referenceModel, null);
            return this.find;
        }

        public SbxQueryBuilder filterWhereIsNull(String field) throws  Exception{
            this.find.setReferenceJoin(OP.IS, field, this.referenceField, this.referenceModel, null);
            return this.find;
        }

        public SbxQueryBuilder filterWhereGreaterThan(String field, Object value) throws  Exception{
            this.find.setReferenceJoin(OP.GT, field, this.referenceField, this.referenceModel, value);
            return this.find;
        }

        public SbxQueryBuilder filterWhereLessThan(String field, Object value) throws  Exception{
            this.find.setReferenceJoin(OP.LT, field, this.referenceField, this.referenceModel, value);
            return this.find;
        }

        public SbxQueryBuilder filterWhereGreaterOrEqualThan(String field, Object value) throws  Exception{
            this.find.setReferenceJoin(OP.GET, field, this.referenceField, this.referenceModel, value);
            return this.find;
        }

        public SbxQueryBuilder filterWhereLessOrEqualThan(String field, Object value) throws  Exception{
            this.find.setReferenceJoin(OP.LET, field, this.referenceField, this.referenceModel, value);
            return this.find;
        }

        public SbxQueryBuilder filterWhereIsNotEqual(String field, Object value) throws  Exception{
            this.find.setReferenceJoin(OP.ISNOT, field, this.referenceField, this.referenceModel, value);
            return this.find;
        }

        public SbxQueryBuilder filterWhereLike(String field, Object value) throws  Exception{
            this.find.setReferenceJoin(OP.LIKE, field, this.referenceField, this.referenceModel, value);
            return this.find;
        }

        public SbxQueryBuilder filterWhereIn(String field, Object value) throws  Exception{
            this.find.setReferenceJoin(OP.IN, field, this.referenceField, this.referenceModel, value);
            return this.find;
        }

        public SbxQueryBuilder filterWhereNotIn(String field, Object value) throws  Exception{
            this.find.setReferenceJoin(OP.NOTIN, field, this.referenceField, this.referenceModel, value);
            return this.find;
        }

    }


    // PUBLIC QUERY METHODS

    public SbxQueryBuilder andWhereIsEqual(String field, Object value) {
        addAND();
        return   whereIsEqual(field,value);
    }

    public SbxQueryBuilder andWhereIsNotNull(String field) {
        addAND();
        return   whereIsNotNull(field );
    }

    public SbxQueryBuilder andWhereGreaterThan(String field, Object value) {
        addAND();
        return   whereGreaterThan(field,value);
    }

    public SbxQueryBuilder andWhereLessThan(String field, Object value) {
        addAND();
        return   whereLessThan(field,value);
    }

    public SbxQueryBuilder andWhereGreaterOrEqualThan(String field, Object value) {
        addAND();
        return   whereGreaterOrEqualThan(field,value);
    }

    public SbxQueryBuilder andWhereLessOrEqualThan(String field, Object value) {
        addAND();
        return   whereLessOrEqualThan(field,value);
    }

    public SbxQueryBuilder andWhereIsNotEqual(String field, Object value) {
        addAND();
        return   whereIsNotEqual(field,value);
    }

    public SbxQueryBuilder andWhereLike(String field, Object value) {
        addAND();
        return   whereLike(field,value);
    }

    public SbxQueryBuilder andWhereIn(String field, Object value) {
        addAND();
        return   whereIn(field,value);
    }


    public SbxQueryBuilder orWhereIsEqual(String field, Object value) {
        addOR();
        return   whereIsEqual(field,value);
    }

    public SbxQueryBuilder orWhereIsNotNull(String field) {
        addOR();
        return   whereIsNotNull(field );
    }

    public SbxQueryBuilder orWhereGreaterThan(String field, Object value) {
        addOR();
        return   whereGreaterThan(field,value);
    }

    public SbxQueryBuilder orWhereLessThan(String field, Object value) {
        addOR();
        return   whereLessThan(field,value);
    }

    public SbxQueryBuilder orWhereGreaterOrEqualThan(String field, Object value) {
        addOR();
        return   whereGreaterOrEqualThan(field,value);
    }

    public SbxQueryBuilder orWhereLessOrEqualThan(String field, Object value) {
        addOR();
        return   whereLessOrEqualThan(field,value);
    }

    public SbxQueryBuilder orWhereIsNotEqual(String field, Object value) {
        addOR();
        return   whereIsNotEqual(field,value);
    }

    public SbxQueryBuilder orWhereLike(String field, Object value) {
        addOR();
        return   whereLike(field,value);
    }

    public SbxQueryBuilder orWhereIn(String field, Object value) {
        addOR();
        return   whereIn(field,value);
    }

    public ReferenceJoin orWhereReferenceJoinBetween(String field, String referenceField) throws Exception {
        return new ReferenceJoin(this, field, referenceField, "OR");
    }

    public ReferenceJoin andWhereReferenceJoinBetween(String field, String referenceField) throws Exception {
        return new ReferenceJoin(this, field, referenceField, "AND");
    }

    public SbxQueryBuilder addRequire(String model) {
        if(row==null) {
            required.add(model);
        }
        return this;
    }

    public SbxQueryBuilder fetch(String[] models) {
        if(row==null) {
            for (String model : models) {
                fetch.add(model);

            }
        }
        return this;
    }


    public ObjectNode compileWithKeys(String[] keys)  {
        if(row==null) {
            if (where.size() > 0) {
                throw new IllegalStateException("");
            }

            for (String key : keys) {
                where.add(key);
            }

            ObjectNode tmp = mapper.createObjectNode();
            tmp.set("keys", where);

            if (required.size() > 0) {
                obj.set("require", required);
            }

            if (fetch.size() > 0) {
                obj.set("fetch", fetch);
            }

            obj.set("where", tmp);
        }
        return obj;
    }

    public ObjectNode compile() throws Exception {

        if(row==null) {
            if (currentGroup!=null && currentGroup.get("GROUP").size() > 0) {
                where.add(currentGroup);
            }

            if (where!=null && where.size() > 0) {
                obj.set("where", where);
            }
            if (referenceJoin!=null ) {
                obj.set("reference_join", referenceJoin);
            }

            if (required!=null && required.size() > 0) {
                obj.set("require", required);
            }

            if (fetch!=null && fetch.size() > 0) {
                obj.set("fetch", fetch);
            }

            if (keysD!=null){
                if(keysD.size()>0){
                    ObjectNode jsonObject= mapper.createObjectNode();
                    jsonObject.set("keys",keysD);
                    obj.put("where",jsonObject);
                }else{
                    throw  new Exception("A Key is required");
                }
            }
        }else{
            if (row.size() > 0) {
                obj.set("rows", row);
            }
        }
        return obj;
    }


}
