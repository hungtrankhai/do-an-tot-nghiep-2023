package com.trankhaihung.cnpm.controller;

import com.trankhaihung.cnpm.entity.Product;
import com.trankhaihung.cnpm.service.ProductService;
import com.trankhaihung.cnpm.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class PhoneController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/")
    public ModelAndView index(Model model){
        ModelAndView mav = new ModelAndView("index");
        List<Product> products = productService.getProducts();
        mav.addObject("products", products);
        Integer numeberOfItemsInCart = shoppingCartService.getNumberOfItemsInCart();
        mav.addObject("numberOfItem", numeberOfItemsInCart);

        return mav;
    }
}
