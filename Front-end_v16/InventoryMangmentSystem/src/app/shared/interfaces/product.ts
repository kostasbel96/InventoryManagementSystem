import { Category } from "./category";
import { Supplier } from "./supplier";

export interface Product{
    id: number,
    name: string,
    description: string | null,
    price: number,
    quantity: number,
    supplier: Supplier | null,
    category: Category | null
}