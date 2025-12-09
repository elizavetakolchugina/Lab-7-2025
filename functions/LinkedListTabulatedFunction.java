package functions;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListTabulatedFunction implements TabulatedFunction, Serializable {
    private static final long serialVersionUID = 3L;

    private class FunctionNode implements Serializable {
        private static final long serialVersionUID = 4L;
        private FunctionPoint point;
        private FunctionNode previous;
        private FunctionNode next;

        public FunctionNode(FunctionPoint point){
            this.point = point;
            this.previous = null;
            this.next = null;
        }

        public FunctionNode(FunctionPoint point, FunctionNode previous, FunctionNode next){
            this.point = point;
            this.previous = previous;
            this.next = next;  
        }

        public FunctionPoint getPoint() {
            return point;
        }

        public void setPoint(FunctionPoint point) {
            this.point = point;
        }

        public FunctionNode getPrevious() {
            return previous;
        }

        public FunctionNode getNext() {
            return next;
        }

        public void setPrevious(FunctionNode previous) {
            this.previous = previous;
        }

        public void setNext(FunctionNode next) {
            this.next = next;
        }
    }

    private FunctionNode head;
    private int pointsCount;
    private FunctionNode lastAccessNode;
    private  int lastAccessIndex;
    private  static double EPSILON = 1e-9;

    public LinkedListTabulatedFunction() {
        emptyList();
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница области определения должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть >=2");
        }

        double distance = (rightX - leftX)/(pointsCount - 1);
        emptyList();

        for (int i = 0; i < pointsCount; i++){
            double x = leftX + i * distance;
            addNodeToTail().setPoint(new FunctionPoint(x, 0.0));
        }
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница области определения должна быть меньше правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть >=2");
        }
        emptyList();
        double distance = (rightX - leftX)/(values.length - 1);
        

        for (int i = 0; i < values.length; i++){
            double x = leftX + i * distance;
            addNodeToTail().setPoint(new FunctionPoint(x, values[i]));
        }
    }

    public LinkedListTabulatedFunction(FunctionPoint[] points){
        if (points.length < 2){
            throw new IllegalArgumentException("Должно быть не менее двух точек");
        }
        for (int i = 1; i < points.length; i++){
            if (points[i] == null || points[i - 1] == null){
                throw new IllegalArgumentException("Точки не могут быть null");
            } 
            if (doubleLessOrEquals(points[i].getX(), points[i - 1].getX())){
                throw new IllegalArgumentException("Точки должны возрастать по абсциссе");
            }
        }
        emptyList();

        for (FunctionPoint point : points) {
            addNodeToTail().setPoint(new FunctionPoint(point));
        }
    }

    public static class LinkedListTabulatedFunctionFactory implements TabulatedFunctionFactory {
        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
            return new LinkedListTabulatedFunction(leftX, rightX, pointsCount);
        }

        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
            return new LinkedListTabulatedFunction(leftX, rightX, values);
        }

        @Override
        public TabulatedFunction createTabulatedFunction(FunctionPoint[] points){
            return new LinkedListTabulatedFunction(points);
        }
    }
    

    private void emptyList() {
        head = new FunctionNode(null);
        head.setPrevious(head);
        head.setNext(head);
        pointsCount = 0;
        lastAccessNode = head;
        lastAccessIndex = -1;
    }

    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " не входит в диапазон");
        }

        if (lastAccessIndex == index && lastAccessNode != head) {
            return lastAccessNode;
        }

        if (lastAccessIndex != -1 && lastAccessIndex == index - 1) {
            lastAccessNode = lastAccessNode.getNext();
            lastAccessIndex = index;
            return lastAccessNode;
        }

        if (lastAccessIndex != -1 && lastAccessIndex == index + 1) {
            lastAccessNode = lastAccessNode.getPrevious();
            lastAccessIndex = index;
            return lastAccessNode;
        }

        FunctionNode current;
        if (index < pointsCount / 2) {
            current = head.getNext();
            for (int i = 0; i < index; i++) {
                current = current.getNext();
            }
        } else {
            current = head.getPrevious();
            for (int i = pointsCount - 1; i > index; i--) {
                current = current.getPrevious();
            }
        }
        lastAccessNode = current;
        lastAccessIndex = index;
        return current;
    }

    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode(null, head.getPrevious(), head);
        head.getPrevious().setNext(newNode);
        head.setPrevious(newNode);
        pointsCount++;

        lastAccessNode = newNode;
        lastAccessIndex = pointsCount - 1;
        return newNode;

    }

    private FunctionNode addNodeByIndex(int index) {
        if (index < 0 || index > pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " не входит в диапазон");
        }
        if (index == pointsCount) {
            return addNodeToTail();
        }

        FunctionNode nodeWithIndex = getNodeByIndex(index);
        FunctionNode newNode = new FunctionNode(null, nodeWithIndex.getPrevious(), nodeWithIndex);

        nodeWithIndex.getPrevious().setNext(newNode);
        nodeWithIndex.setPrevious(newNode);
        pointsCount++;
        lastAccessIndex = index;
        lastAccessNode = newNode;
        return newNode;
    }

    private FunctionNode deleteNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " не входит в диапазон");
        }

        FunctionNode deletedNode = getNodeByIndex(index);

        deletedNode.getPrevious().setNext(deletedNode.getNext());
        deletedNode.getNext().setPrevious(deletedNode.getPrevious());
        pointsCount--;

        if (lastAccessIndex == index) {
            lastAccessNode = head;
            lastAccessIndex = -1;
        } else if (lastAccessIndex > index) {
            lastAccessIndex--;
        }

        return deletedNode;
    }




    //методы TabulatedFunction
    @Override
    public double getLeftDomainBorder(){
        if (pointsCount == 0) {
            throw new IllegalStateException("Функция не содержит точек");
        }
        return head.getNext().getPoint().getX();
    }

    @Override
    public double getRightDomainBorder() {
        if (pointsCount == 0) {
            throw new IllegalStateException("Функция не содержит точек");
        }
        return head.getPrevious().getPoint().getX();
    }

    @Override
    public double getFunctionValue(double x){
        if (doubleLess(x, getLeftDomainBorder()) || doubleGreater(x, getRightDomainBorder()) || pointsCount == 0) {
            return Double.NaN;
        }

        FunctionNode current;
        if (lastAccessNode != head && doubleGreaterOrEquals(x, lastAccessNode.getPoint().getX())) {
            current = lastAccessNode;
        } else {
            current = head.getNext();
        }

        while (current != head && current.getNext() != head) {
            double x_1 = current.getPoint().getX();
            double x_2 = current.getNext().getPoint().getX();

            if (doubleEquals(x, x_1)){
                lastAccessNode = current;
                lastAccessIndex = getNodeIndex(current);
                return current.getPoint().getY();
            }

            if (doubleEquals(x, x_2)){
                lastAccessNode = current.getNext();
                lastAccessIndex = getNodeIndex(current.getNext());           
                return current.getNext().getPoint().getY();
            }

            if (doubleGreater(x, x_1) && doubleLess(x, x_2)){
                double y_1 = current.getPoint().getY();
                double y_2 = current.getNext().getPoint().getY();

                return (x - x_1) * (y_2 - y_1) / (x_2 - x_1) + y_1;
            }

            current = current.getNext();                                     
        }
        return Double.NaN;
    }

    @Override
    public int getPointsCount(){
        return pointsCount;
    }

    @Override
    public FunctionPoint getPoint(int index){
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " не входит в диапазон");
        }
        FunctionNode node = getNodeByIndex(index);
        return new FunctionPoint(node.getPoint());
    }

    @Override
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException{
        if (point == null) {
            throw new IllegalArgumentException("Точка без значения");
        }

        FunctionNode node = getNodeByIndex(index);
        
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " не входит в диапазон");
        }
        if (index > 0 && doubleLessOrEquals(point.getX(), node.getPrevious().getPoint().getX())) {
            throw new InappropriateFunctionPointException("Новая точка должна быть больше предыдущей по x");
        }
        if (index < pointsCount - 1 && doubleGreaterOrEquals(point.getX(),node.getNext().getPoint().getX())) {
            throw new InappropriateFunctionPointException("Новая точка должна быть меньше следующей по x");
        }
        
        node.setPoint(new FunctionPoint(point));
    }
    
    @Override
    public double getPointX(int index){
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " не входит в диапазон");
        }
        return getNodeByIndex(index).getPoint().getX();
    }

    @Override
    public void setPointX(int index, double x) throws InappropriateFunctionPointException{
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " не входит в диапазон");
        }

        FunctionNode node = getNodeByIndex(index);
        FunctionPoint currentPoint = node.getPoint();

        if (index > 0 && doubleLessOrEquals(x, node.getPrevious().getPoint().getX())) {
            throw new InappropriateFunctionPointException("Новая точка должна быть больше предыдущей по x");
        }
        if (index < pointsCount - 1 && doubleGreaterOrEquals(x, node.getNext().getPoint().getX())) {
            throw new InappropriateFunctionPointException("Новая точка должна быть меньше следующей по x"); 
        }
        
        node.setPoint(new FunctionPoint(x, currentPoint.getY()));
    }

    @Override
    public double getPointY(int index){
        if (index <0 || index >= pointsCount){
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " не входит в диапазон");
        }
        return getNodeByIndex(index).getPoint().getY();
    }

    @Override
    public void setPointY(int index, double y) {
        if (index < 0 || index >= pointsCount){
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " не входит в диапазон");
        }

        FunctionNode node = getNodeByIndex(index);
        FunctionPoint currentPoint = node.getPoint();
        node.setPoint(new FunctionPoint(currentPoint.getX(), y));
    }

    @Override
    public void deletePoint(int index) {
        if (pointsCount <= 2) {
            throw new IllegalStateException("Минимальное количество точек: 2");
        }
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " не входит в диапазон");
        }

        deleteNodeByIndex(index);
    }

    @Override
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        if (point == null) {
            throw new IllegalArgumentException("Точка без значения");
        }

        FunctionNode current = head.getNext();
        int newIndex = 0;

        while (current != head && doubleLess(current.getPoint().getX(), point.getX())) {
            current = current.getNext();
            newIndex++;
        }
        if  (newIndex < pointsCount && doubleEquals(current.getPoint().getX(), point.getX())) {
            throw new InappropriateFunctionPointException("Такая точка уже существует");

        }
        FunctionNode newNode = addNodeByIndex(newIndex);
        newNode.setPoint(new FunctionPoint(point));
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("{");

        FunctionNode current = head.getNext();
        while (current != head) {
            str.append("(").append(current.getPoint().getX()).append("; ").append(current.getPoint().getY()).append(")");
            if (current.getNext() != head){
                str.append(",");
            }
            current = current.getNext();
        }
        str.append("}");
        return str.toString();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TabulatedFunction)) {
            return false;
        }

        TabulatedFunction newFunc = (TabulatedFunction) o;
        if (this.getPointsCount() != newFunc.getPointsCount()) {
            return false;
        }
        if (o instanceof LinkedListTabulatedFunction) {
            LinkedListTabulatedFunction newList = (LinkedListTabulatedFunction) o;
            FunctionNode thisNode = head.getNext();
            FunctionNode newNode = newList.head.getNext();
            while (thisNode != head && newNode != newList.head) {
                if (!doubleEquals(thisNode.getPoint().getX(), newNode.getPoint().getX())){
                    return false;
                }
                if (!doubleEquals(thisNode.getPoint().getY(), newNode.getPoint().getY())) {
                    return false;
                }
                thisNode = thisNode.getNext();
                newNode = newNode.getNext();
            }
            return true;
            
        } else {
            for (int i = 0; i < getPointsCount(); i++) {
                FunctionPoint thisPoint = this.getPoint(i);
                FunctionPoint newPoint = newFunc.getPoint(i);
                if (!doubleEquals(thisPoint.getX(), newPoint.getX())){
                    return false;
                }
                if (!doubleEquals(thisPoint.getY(), newPoint.getY())) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = getPointsCount(); // Включаем количество точек в хэш
        FunctionNode current = head.getNext();
        while (current != head) {
            hash ^= current.getPoint().hashCode();
            current = current.getNext();
        }
        return hash;
    }

    @Override
    public Object clone() {
        LinkedListTabulatedFunction cloned = new LinkedListTabulatedFunction();
        FunctionNode current = head.getNext();
        while (current != head) {
            FunctionNode newNode = cloned.addNodeToTail();
            newNode.setPoint((FunctionPoint) current.getPoint().clone());
            current = current.getNext();
        }
        return  cloned;        
    }

    @Override
    public Iterator<FunctionPoint> iterator() {
        return new Iterator<FunctionPoint>() {
            private FunctionNode currentNode = head.getNext();

            @Override
            public boolean hasNext() {
                return currentNode != head;
            }

            @Override
            public FunctionPoint next() {
                if(!hasNext()) {
                    throw new NoSuchElementException ("В табулированной функции кончились точки");
                }
                FunctionPoint point = new FunctionPoint(currentNode.getPoint());
                currentNode = currentNode.getNext();
                return point;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Операция удаления не поддерживается");
            }

        };
    }

    private int getNodeIndex(FunctionNode node) {
        FunctionNode current = head.getNext();
        int index = 0;
        while (current != head) {
            if (current == node){
                return index;
            }
            current = current.getNext();
            index++;
        }
        throw new IllegalStateException ("Узел не найден в списке");
    }

    private boolean doubleEquals(double a, double b) {
        return Math.abs(a - b) < EPSILON;
    }

    private boolean doubleLess(double a, double b) {
        return a < b - EPSILON;
    }

    private boolean doubleGreater(double a, double b) {
        return a > b + EPSILON;
    }
     private boolean doubleLessOrEquals(double a, double b) {
        return a < b + EPSILON;
    }

    private boolean doubleGreaterOrEquals(double a, double b) {
        return a > b - EPSILON;
    }

}