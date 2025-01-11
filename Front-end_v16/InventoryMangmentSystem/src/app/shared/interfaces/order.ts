import { OrderItem } from "./order-item";
import { Product } from "./product";
import { Supplier } from "./supplier";

export interface Order{
    id: number,
    supplier: Supplier | null,
    orderDate: string,
    orderItems: OrderItem[] | null
} 