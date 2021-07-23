class LinkedList <T> {

    public node head = null;

    class node {
        public double priority;
        T data;
        public node next;

        public node(int priority, T data){
            this.priority = priority;
            this.data = data;
        }
        public node(T data, double priority){
            this.priority = priority;
            this.data = data;
        }
    }

    /**
     * This method returns the first element in the list, simlar to "deleteMin"
     * @return first element data of type <T>.
     */
    public T poll(){
        T val;
        if (head==null){
            head=null;
            return null;
        }
        if (head.next==null){
            val = head.data;
            head=null;
            return val;
        }
        else {
            val = head.data;
            head=head.next;
            return val;
        }
    }

    /**
     * This method inserts values into a priority queue and sorts by the priority Max -> Min
     * Trims list if size is greater than 20. used specfically for TOP 20 method in main.
     * @param data Sorts stored data of specificed type T
     * @param priority Sorts stored priority of type int by big -> small
     */
    public void insert(T data, int priority) {
        node nodeToAdd = new node (priority,data);

        node current = head;
        node temp;

        if (head == null) {
            head = nodeToAdd;
            return;
        }
        else if (nodeToAdd.priority > head.priority){
            head = nodeToAdd;
            head.next = current;
            return;
        }
        else{
            while (current != null) {

                if (current.next!=null && current.next.priority < nodeToAdd.priority){
                    temp = current.next;
                    current.next = nodeToAdd;
                    nodeToAdd.next = temp;
                    break;
                }
                if (current.next == null) {
                    current.next = nodeToAdd;
                    break;
                }
                current = current.next;
            }
        }
        if (getLength() > 20){
            current = head;
            for (int i=0;i<19;i++){
                current = current.next;
            }
            current.next = null;
        }
    }

    /**
     * This method inserts values into a priority queue and sorts by the priority Min -> Max
     * Trims list if size is greater than 20. used specfically for TOP 20 method in main.
     * @param data Sorts stored data of specificed type T
     * @param priority Sorts stored priority of type int by small -> big
     */
    public void add(T data, double priority) {
        node nodeToAdd = new node (data,priority);

        node current = head;
        node temp;

        if (head == null) {
            head = nodeToAdd;
            return;
        }
        else if (nodeToAdd.priority < head.priority){
            head = nodeToAdd;
            head.next = current;
            return;
        }
        else{
            while (current != null) {
                if (current.next!=null && current.next.priority > nodeToAdd.priority){
                    temp = current.next;
                    current.next = nodeToAdd;
                    nodeToAdd.next = temp;
                    break;
                }
                if (current.next == null) {
                    current.next = nodeToAdd;
                    break;
                }
                current = current.next;
            }
        }
    }
    public double getLargestModule(){
        return head.priority;
    }


    public int getLength() {
        int c = 0;
        node current = head;
        while (current != null) {
            c++;
            current = current.next;
        }
        return c;
    }
    public void printSubTreeSizes(){
        node current = head;
        while (current!=null){
            System.out.print((int)current.priority+" ");
            current = current.next;
        }
    }
    public boolean isEmpty(){
        if (head==null){
            return true;
        }
        else{
            return false;
        }
    }
}
